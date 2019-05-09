/*
 * Copyright 2015-2019 ZJNU ACM.
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

import cn.edu.zjnu.acm.judge.domain.SubmissionDetail
import cn.edu.zjnu.acm.judge.exception.BusinessCode
import cn.edu.zjnu.acm.judge.exception.BusinessException
import cn.edu.zjnu.acm.judge.mapper.SubmissionDetailMapper
import cn.edu.zjnu.acm.judge.mapper.SubmissionMapper
import cn.edu.zjnu.acm.judge.mapper.UserProblemMapper
import cn.edu.zjnu.acm.judge.service.*
import cn.edu.zjnu.acm.judge.support.JudgeData
import cn.edu.zjnu.acm.judge.util.ResultType
import cn.edu.zjnu.acm.judge.core.SimpleValidator
import cn.edu.zjnu.acm.judge.core.Status
import cn.edu.zjnu.acm.judge.core.Validator
import cn.edu.zjnu.acm.judge.sandbox.win32.SpecialValidator
import cn.edu.zjnu.acm.judge.support.RunRecord
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.io.PrintWriter
import java.io.StringWriter

/**
 *
 * @author zhanhb
 */
@Service("judgeService")
class JudgeServiceImpl(
        @Suppress("SpringJavaInjectionPointsAutowiringInspection") private val submissionMapper: SubmissionMapper,
        @Suppress("SpringJavaInjectionPointsAutowiringInspection") private val submissionDetailMapper: SubmissionDetailMapper,
        @Suppress("SpringJavaInjectionPointsAutowiringInspection") private val userProblemMapper: UserProblemMapper,
        private val problemService: ProblemService,
        private val systemService: SystemService,
        private val languageService: LanguageService,
        private val judgeRunner: JudgeRunner
) : JudgeService {

    private fun updateSubmissionStatus(userId: String, problemId: Long) {
        userProblemMapper.update(userId, problemId)
        userProblemMapper.updateUser(userId)
        userProblemMapper.updateProblem(problemId)
    }


    override fun execute(submissionId: Long) {
        val submission = submissionMapper.findOne(submissionId)
                ?: throw  BusinessException(BusinessCode.SUBMISSION_NOT_FOUND)
        val problemId = submission.problem
        val problem = problemService.findOneNoI18n(problemId);
        try {
            val runRecord = RunRecord(
                    language = languageService.getAvailableLanguage(submission.language),
                    source = submissionDetailMapper.findSourceById(submissionId),
                    memoryLimit = problem.memoryLimit!!,
                    timeLimit = problem.timeLimit!!
            )
            val dataDirectory = systemService.getDataDirectory(problemId);
            val judgeData = JudgeData(dataDirectory);
            val specialFile = systemService.getSpecialJudgeExecutable(problemId);
            val isSpecial = systemService.isSpecialJudge(problemId);
            val work = systemService.getWorkDirectory(submissionId); //建立临时文件
            val validator: Validator = if (isSpecial)
                SpecialValidator(specialFile.toString(), work)
            else SimpleValidator.PE_AS_ACCEPTED
            val deleteTempFile = systemService.isDeleteTempFile;
            val runResult = judgeRunner.run(runRecord, work, judgeData, validator, deleteTempFile);
            val detail = SubmissionDetail(
                    id = submissionId,
                    compileInfo = runResult.compileInfo,
                    detail = runResult.detail,
                    systemInfo = runResult.systemInfo
            )
            if (runResult.type == Status.COMPILATION_ERROR) {
                submissionMapper.updateResult(submissionId, ResultType.COMPILE_ERROR, 0, 0);
                submissionDetailMapper.update(detail);
            } else {
                val score = runResult.score;
                val time = runResult.time;
                val memory = runResult.memory;
                submissionMapper.updateResult(submissionId, score, time, memory);
                submissionDetailMapper.update(detail);
            }
            updateSubmissionStatus(submission.user!!, problemId);
        } catch (ex: Throwable) {
            log.error("got an exception when judging submission {}", submissionId, ex);
            submissionMapper.updateResult(submissionId, ResultType.SYSTEM_ERROR, 0, 0);
            val sw = StringWriter();
            PrintWriter(sw).use { pw ->
                ex.printStackTrace(pw);
            }
            submissionDetailMapper.update(SubmissionDetail(
                    id = submissionId,
                    systemInfo = sw.toString()
            ))
        }
    }

    companion object {
        private val log = LoggerFactory.getLogger(JudgeServiceImpl::class.java)
    }
}
