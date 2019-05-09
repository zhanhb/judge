/*
 * Copyright 2019 ZJNU ACM.
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
package cn.edu.zjnu.acm.judge.service

import cn.edu.zjnu.acm.judge.data.form.ContestForm
import cn.edu.zjnu.acm.judge.data.form.ContestStatus
import cn.edu.zjnu.acm.judge.domain.Contest
import cn.edu.zjnu.acm.judge.domain.Problem
import cn.edu.zjnu.acm.judge.exception.BusinessException
import cn.edu.zjnu.acm.judge.service.impl.UserStanding
import cn.edu.zjnu.acm.judge.util.SpecialCall
import java.io.IOException
import java.util.Locale
import java.util.concurrent.CompletableFuture

/**
 *
 * @author zhanhb
 */
interface ContestService {

    @Throws(IOException::class)
    fun delete(id: Long)

    fun findAll(form: ContestForm): List<Contest>

    fun findAll(first: ContestStatus, vararg rest: ContestStatus): List<Contest>

    @Throws(BusinessException::class)
    fun findOneByIdAndNotDisabled(contestId: Long): Contest

    /**
     * throws BusinessException if contest not exists.
     *
     * @param contestId
     * @param locale
     * @return
     */
    fun getContestAndProblems(contestId: Long, locale: Locale): Contest

    fun getContestAndProblemsNotDisabled(contestId: Long, userId: String?, locale: Locale?): Contest

    fun getProblem(contestId: Long, problemNum: Long, locale: Locale?): Problem

    fun getProblemsMap(id: Long): Map<Long, LongArray>

    @SpecialCall("contests/problems")
    fun getStatus(contest: Contest): String

    fun addProblem(contestId: Long, problemId: Long)

    fun save(contest: Contest): Contest

    fun standing(id: Long): List<UserStanding>

    fun standingAsync(id: Long): CompletableFuture<List<UserStanding>>

    @SpecialCall("contests/problem", "contests/problems", "fragment/standing")
    fun toProblemIndex(num: Long): String

    fun updateSelective(id: Long, contest: Contest)

}
