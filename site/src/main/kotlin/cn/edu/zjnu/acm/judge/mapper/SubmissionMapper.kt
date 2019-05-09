/*
 * Copyright 2015 ZJNU ACM.
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
package cn.edu.zjnu.acm.judge.mapper

import cn.edu.zjnu.acm.judge.data.dto.ScoreCount
import cn.edu.zjnu.acm.judge.data.form.BestSubmissionForm
import cn.edu.zjnu.acm.judge.data.form.SubmissionQueryForm
import cn.edu.zjnu.acm.judge.domain.Submission
import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.annotations.Param
import org.springframework.data.domain.Pageable

/**
 *
 * @author zhanhb
 */
@Mapper
interface SubmissionMapper {

    fun save(submission: Submission): Long

    fun findOne(@Param("id") id: Long): Submission?

    fun updateResult(
            @Param("id") id: Long,
            @Param("score") score: Int,
            @Param("time") time: Long,
            @Param("memory") memory: Long): Long

    fun findAllByCriteria(submissionQueryForm: SubmissionQueryForm): List<Submission>

    fun findAllByProblemIdAndResultNotAccept(@Param("problemId") problemId: Long): List<Long>

    fun bestSubmission(@Param("form") form: BestSubmissionForm, @Param("pageable") pageable: Pageable): List<Submission>

    fun groupByScore(
            @Param("contestId") contestId: Long?,
            @Param("problemId") problemId: Long): List<ScoreCount>

    fun clearByContestId(@Param("contest") contest: Long): Long

    fun findAllByContestId(@Param("contest") id: Long): List<Long>

    fun delete(@Param("id") id: Long): Long
}
