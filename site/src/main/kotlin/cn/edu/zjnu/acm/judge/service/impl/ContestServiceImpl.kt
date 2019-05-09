/*
 * Copyright 2016-2019 ZJNU ACM.
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

import cn.edu.zjnu.acm.judge.data.dto.Standing
import cn.edu.zjnu.acm.judge.data.form.ContestForm
import cn.edu.zjnu.acm.judge.data.form.ContestStatus
import cn.edu.zjnu.acm.judge.domain.Contest
import cn.edu.zjnu.acm.judge.domain.Problem
import cn.edu.zjnu.acm.judge.domain.User
import cn.edu.zjnu.acm.judge.exception.BusinessCode
import cn.edu.zjnu.acm.judge.exception.BusinessException
import cn.edu.zjnu.acm.judge.mapper.ContestMapper
import cn.edu.zjnu.acm.judge.mapper.SubmissionMapper
import cn.edu.zjnu.acm.judge.service.ContestService
import cn.edu.zjnu.acm.judge.service.LocaleService
import cn.edu.zjnu.acm.judge.util.EnumUtils
import cn.edu.zjnu.acm.judge.util.SpecialCall
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.common.collect.ImmutableMap
import com.google.common.collect.Maps
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.io.IOException
import java.time.Instant
import java.util.EnumSet
import java.util.Locale
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger

/**
 *
 * @author zhanhb
 */
@Service("contestService")
@SpecialCall("contests/problems")
class ContestServiceImpl(
        @Suppress("SpringJavaInjectionPointsAutowiringInspection") private val contestMapper: ContestMapper,
        @Suppress("SpringJavaInjectionPointsAutowiringInspection") private val submissionMapper: SubmissionMapper,
        private val localeService: LocaleService,
        private val objectMapper: ObjectMapper
) : ContestService {

    override fun getStatus(contest: Contest): String {
        val disabled = contest.disabled
        return if (disabled != null && disabled) {
            "Disabled"
        } else if (contest.isError) {
            "Error"
        } else if (!contest.isStarted) {
            "Pending"
        } else if (!contest.isEnded) {
            "Running"
        } else {
            "Ended"
        }
    }

    override fun addProblem(contestId: Long, problemId: Long) {
        contestMapper.addProblem(contestId, problemId, null)
    }

    override fun findAll(form: ContestForm): List<Contest> {
        val exclude = ContestStatus.parse(form.exclude)
        val include = ContestStatus.parse(form.include)
        val result: EnumSet<ContestStatus>
        if (!exclude.isEmpty()) {
            result = EnumSet.allOf(ContestStatus::class.java)
            result.removeAll(exclude)
        } else if (!include.isEmpty()) {
            result = include
        } else {
            result = EnumSet.allOf(ContestStatus::class.java)
        }
        return contestMapper.findAllByQuery(form.includeDisabled, EnumUtils.toMask(result))
    }

    override fun findAll(first: ContestStatus, vararg rest: ContestStatus): List<Contest> {
        return contestMapper.findAllByQuery(false, EnumUtils.toMask(EnumSet.of(first, *rest)))
    }

    @Transactional
    override fun save(contest: Contest): Contest {
        contestMapper.save(contest)
        // after saving, id should present
        val id = contest.id!!
        val problems = contest.problems
        if (problems != null) {
            add(id, problems)
        }
        return contest
    }

    override fun getContestAndProblems(contestId: Long, locale: Locale): Contest {
        val contest = checkedGet(contestId)
        val problems = contestMapper.getProblems(contestId, null, localeService.resolve(locale))
        return contest.copy(problems = problems)
    }

    override fun getContestAndProblemsNotDisabled(contestId: Long, userId: String?, locale: Locale?): Contest {
        val contest = getEnabledContest(contestId)
        val problems = contestMapper.getProblems(contestId, userId, localeService.resolve(locale))
        return contest.copy(problems = problems)
    }

    private fun getEnabledContest(contestId: Long): Contest {
        return contestMapper.findOneByIdAndNotDisabled(contestId)
                ?: throw BusinessException(BusinessCode.CONTEST_NOT_FOUND, contestId)
    }

    private fun checkedGet(contestId: Long): Contest {
        return contestMapper.findOne(contestId)
                ?: throw BusinessException(BusinessCode.CONTEST_NOT_FOUND, contestId)
    }

    @Transactional
    @Throws(IOException::class)
    override fun delete(id: Long) {
        if (log.isWarnEnabled) {
            val submissions = submissionMapper.findAllByContestId(id)
            val problems = contestMapper.getProblems(id, null, null)
            log.warn("delete contest id: {}, submissions: {}, problems: {}", id, objectMapper.writeValueAsString(submissions), objectMapper.writeValueAsString(problems))
        }
        val result = (submissionMapper.clearByContestId(id)
                + contestMapper.deleteContestProblems(id)
                + contestMapper.deleteByPrimaryKey(id))
        if (result == 0L) {
            throw BusinessException(BusinessCode.CONTEST_NOT_FOUND, id)
        }
    }

    @Transactional
    override fun updateSelective(id: Long, contest: Contest) {
        val copy = contest.copy(createdTime = null, modifiedTime = Instant.now())
        val result = contestMapper.updateSelective(id, copy)
        if (result == 0L) {
            throw BusinessException(BusinessCode.CONTEST_NOT_FOUND, id)
        }
        val problems = copy.problems
        if (problems != null) {
            contestMapper.deleteContestProblems(id)
            add(id, problems)
        }
    }

    override fun getProblemsMap(id: Long): Map<Long, LongArray> {
        val problems = contestMapper.getProblems(id, null, null)
        val atomic = AtomicInteger()
        return problems.stream().collect(ImmutableMap.toImmutableMap({ p: Problem -> p.origin!! },
                { problem -> longArrayOf(atomic.getAndIncrement().toLong(), problem.id!!) }
        ))
    }

    private fun add(contestId: Long, problems: List<Problem>) {
        if (problems.isNotEmpty()) {
            val array = problems.stream().mapToLong({ p: Problem -> p.origin!! }).toArray()
            contestMapper.addProblems(contestId, 1000, array)
        }
    }

    override fun toProblemIndex(num: Long): String {
        val t = ('A'.toLong() + num).toChar()
        return t.toString()
    }

    override fun standing(id: Long): List<UserStanding> {
        val hashMap = Maps.newHashMapWithExpectedSize<String, UserStanding>(80)
        contestMapper.standing(id).forEach { standing: Standing? ->
            hashMap.computeIfAbsent(standing!!.user) { user: String -> UserStanding(user) }
                    .add(standing.problem, standing.time, standing.penalty)
        }
        contestMapper.attenders(id).forEach { attender: User ->
            hashMap[attender.id]?.nick = attender.nick
        }
        val standings = hashMap.values.stream()
                .sorted(UserStanding.COMPARATOR).toArray<UserStanding> {
                    arrayOfNulls<UserStanding>(it)
                }
        this.setIndexes(standings, Comparator.nullsFirst(UserStanding.COMPARATOR)) { us, index -> us.index = index }
        return listOf(*standings)
    }

    override fun standingAsync(id: Long): CompletableFuture<List<UserStanding>> {
        return STANDINGS.computeIfAbsent(id) { contestId ->
            CompletableFuture.supplyAsync<List<UserStanding>> {
                val result = standing(contestId)
                STANDINGS.remove(id)
                result
            }
        }
    }

    private fun <T> setIndexes(standings: Array<T>, c: Comparator<T>, consumer: (T, Int) -> Unit) {
        var i = 0
        val len = standings.size
        var lastIndex = 0

        var last: T? = null
        var standing: T
        while (i < len) {
            standing = standings[i++]
            if (c.compare(standing, last) != 0) {
                lastIndex = i
            }
            consumer(standing, lastIndex)
            last = standing
        }
    }

    override fun getProblem(contestId: Long, problemNum: Long, locale: Locale?): Problem {
        return contestMapper.getProblem(contestId, problemNum, localeService.resolve(locale))
                ?: throw BusinessException(BusinessCode.CONTEST_PROBLEM_NOT_FOUND, contestId, problemNum)
    }

    override fun findOneByIdAndNotDisabled(contestId: Long): Contest {
        return getEnabledContest(contestId)
    }

    companion object {
        private val log = LoggerFactory.getLogger(ContestServiceImpl::class.java)
        private val STANDINGS = ConcurrentHashMap<Long, CompletableFuture<List<UserStanding>>>(20)
    }

}
