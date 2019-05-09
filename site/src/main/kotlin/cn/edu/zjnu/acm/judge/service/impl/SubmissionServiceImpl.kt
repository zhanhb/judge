/*
 * Copyright 2016 ZJNU ACM.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.edu.zjnu.acm.judge.service.impl

import cn.edu.zjnu.acm.judge.data.dto.SubmissionDetailDTO
import cn.edu.zjnu.acm.judge.data.form.BestSubmissionForm
import cn.edu.zjnu.acm.judge.domain.Submission
import cn.edu.zjnu.acm.judge.exception.BusinessCode
import cn.edu.zjnu.acm.judge.exception.BusinessException
import cn.edu.zjnu.acm.judge.mapper.*
import cn.edu.zjnu.acm.judge.service.*
import cn.edu.zjnu.acm.judge.util.ResultType
import com.google.common.annotations.VisibleForTesting
import com.google.common.cache.CacheBuilder
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeUnit
import javax.servlet.http.HttpServletRequest

/**
 *
 * @author zhanhb
 */
@Service("submissionService")
class SubmissionServiceImpl(
        @Suppress("SpringJavaInjectionPointsAutowiringInspection") private val contestMapper: ContestMapper,
        @Suppress("SpringJavaInjectionPointsAutowiringInspection") private val problemMapper: ProblemMapper,
        @Suppress("SpringJavaInjectionPointsAutowiringInspection") private val submissionMapper: SubmissionMapper,
        @Suppress("SpringJavaInjectionPointsAutowiringInspection") private val submissionDetailMapper: SubmissionDetailMapper,
        @Suppress("SpringJavaInjectionPointsAutowiringInspection") private val userPerferenceMapper: UserPreferenceMapper,
        private val judgePoolService: JudgePoolService,
        private val problemService: ProblemService,
        private val languageService: LanguageService,
        private val contestService: ContestService
) : SubmissionService {

    private val cache = Collections.newSetFromMap(CacheBuilder.newBuilder().expireAfterWrite(SUBMIT_INTERVAL.toLong(), TimeUnit.SECONDS).build<String, Boolean>().asMap())

    override fun canView(request: HttpServletRequest, submission: Submission): Boolean {
        if (UserDetailsServiceImpl.isAdminLoginned(request)) {
            return true
        }
        // TODO cast to Authentication
        if (UserDetailsServiceImpl.isUser(request.userPrincipal as Authentication?, submission.user)) {
            return true
        }
        val sourceBrowser = UserDetailsServiceImpl.isSourceBrowser(request)
        if (sourceBrowser) {
            val contestId = submission.contest ?: return true
            val contest = contestMapper.findOne(contestId)
            return contest == null || contest.isEnded
        }
        return false
    }

    override fun bestSubmission(contestId: Long?, problemId: Long, pageable: Pageable, total: Long): Page<Submission> {
        val form = BestSubmissionForm(contestId = contestId, problemId = problemId)
        val bestSubmissions = submissionMapper.bestSubmission(form, pageable)
        return PageImpl(bestSubmissions, pageable, total)
    }

    private fun check(languageId: Int, source: String, userId: String) {
        languageService.getAvailableLanguage(languageId)
        if (source.length > 32768) {
            throw BusinessException(BusinessCode.SOURCE_CODE_LONG)
        }
        if (source.length < 10) {
            throw BusinessException(BusinessCode.SOURCE_CODE_SHORT)
        }
        // 10秒交一次。。。
        if (cache.contains(userId) || !cache.add(userId)) {
            throw BusinessException(BusinessCode.SUBMISSION_FREQUENTLY, SUBMIT_INTERVAL)
        }
    }

    override fun submit(languageId: Int, source: String, userId: String,
                        ip: String, problemId: Long, addToPool: Boolean): CompletableFuture<*> {
        check(languageId, source, userId)
        problemService.findOneNoI18n(problemId) //检查该题是否存在
        return submit0(null, problemId, ip, source, userId, languageId, addToPool)
    }

    override fun contestSubmit(languageId: Int, source: String, userId: String, ip: String, contestId: Long, problemNum: Long): CompletableFuture<*> {
        check(languageId, source, userId)
        val contest = contestService.findOneByIdAndNotDisabled(contestId)
        // contest not started yet, can't submit the problem.
        if (!contest.isStarted) {
            throw BusinessException(BusinessCode.CONTEST_PROBLEM_NOT_FOUND, contestId, problemNum)
        }
        val problem = contestService.getProblem(contestId, problemNum, null)
        return submit0(if (contest.isEnded) null else contestId,
                problem.origin!!,
                ip, source, userId, languageId, true)
    }

    private fun submit0(contestId: Long?, problemId: Long, ip: String?, source: String,
                        userId: String, languageId: Int, addToPool: Boolean): CompletableFuture<*> {
        val now = Instant.now()
        val submission = Submission(
                contest = contestId,
                problem = problemId,
                ip = ip,
                user = userId,
                inDate = now,
                sourceLength = source.length,
                language = languageId,
                score = ResultType.QUEUING
        )
        // 插入solution数据库表
        submissionMapper.save(submission)
        val submissionId = submission.id
        problemMapper.setInDate(submission.problem, now)

        // 插入source_code表
        submissionDetailMapper.saveSource(submissionId, source)
        userPerferenceMapper.setLanguage(userId, languageId)
        return if (addToPool) judgePoolService.add(submissionId)
        else CompletableFuture.completedFuture<Any>(null)
    }

    override fun findCompileInfo(submissionId: Long): String? {
        submissionMapper.findOne(submissionId)
                ?: throw BusinessException(BusinessCode.SUBMISSION_NOT_FOUND, submissionId)
        return submissionDetailMapper.findCompileInfoById(submissionId)
    }

    @Transactional
    override fun delete(id: Long) {
        val result = submissionDetailMapper.delete(id) + submissionMapper.delete(id)
        if (result == 0L) {
            throw BusinessException(BusinessCode.SUBMISSION_NOT_FOUND, id)
        }
    }

    @VisibleForTesting
    override fun remove(userId: String) {
        cache.remove(userId)
    }

    override fun parseSubmissionDetail(message:String):List<SubmissionDetailDTO>  {
        val detailsArray = message.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        return (0 until detailsArray.size / 4).map { i ->
            SubmissionDetailDTO(
                    result = ResultType.getCaseScoreDescription(Integer.parseInt(detailsArray[i shl 2])),
                    score = detailsArray[i shl 2 or 1],
                    time = detailsArray[i shl 2 or 2],
                    memory = detailsArray[i shl 2 or 3]
            )
        }
    }

    override fun getSubmissionDetail(submissionId: Long): List<SubmissionDetailDTO> {
        val submissionDetail = submissionDetailMapper.getSubmissionDetail(submissionId) ?: return listOf()
        return parseSubmissionDetail(submissionDetail)
    }

    companion object {
        private val SUBMIT_INTERVAL = 10
    }

}
