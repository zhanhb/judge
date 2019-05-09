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

import cn.edu.zjnu.acm.judge.data.dto.Standing
import cn.edu.zjnu.acm.judge.domain.Contest
import cn.edu.zjnu.acm.judge.domain.Problem
import cn.edu.zjnu.acm.judge.domain.User
import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.annotations.Param

/**
 *
 * @author zhanhb
 */
@Mapper
interface ContestMapper {

    fun save(contest: Contest): Long

    fun getProblems(
            @Param("contest") contestId: Long,
            @Param("userId") userId: String?,
            @Param("lang") lang: String?): List<Problem>

    fun standing(@Param("id") contestId: Long): List<Standing>

    fun findOne(@Param("id") contestId: Long): Contest?

    fun getProblem(
            @Param("contest") contestId: Long,
            @Param("problem") problemNum: Long,
            @Param("lang") lang: String?): Problem?

    fun findOneByIdAndNotDisabled(@Param("id") contestId: Long): Contest?

    fun attenders(@Param("id") contestId: Long): List<User>

    fun addProblem(@Param("id") contestId: Long, @Param("problem") problem: Long,
                   @Param("title") title: String?): Long

    fun addProblems(@Param("contestId") contestId: Long, @Param("base") base: Int, @Param("problems") problems: LongArray): Long

    fun findAllByQuery(@Param("includeDisabled") includeDisabled: Boolean, @Param("mask") mask: Int): List<Contest>

    fun deleteContestProblems(@Param("id") id: Long): Long

    fun deleteByPrimaryKey(@Param("id") id: Long): Long

    fun updateSelective(@Param("id") id: Long, @Param("c") contest: Contest): Long

}
