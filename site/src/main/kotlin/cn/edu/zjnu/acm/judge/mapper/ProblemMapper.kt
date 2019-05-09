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

import cn.edu.zjnu.acm.judge.data.form.ProblemForm
import cn.edu.zjnu.acm.judge.domain.Problem
import java.time.Instant
import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.annotations.Param
import org.springframework.data.domain.Pageable

/**
 *
 * @author zhanhb
 */
@Mapper
interface ProblemMapper {

    fun save(problem: Problem): Long

    fun findOne(@Param("id") id: Long, @Param("lang") lang: String?): Problem?

    fun findOneNoI18n(@Param("id") id: Long): Problem?

    fun setInDate(@Param("id") problemId: Long, @Param("inDate") timestamp: Instant): Long

    fun updateSelective(@Param("id") id: Long, @Param("p") build: Problem, @Param("lang") lang: String?): Long

    fun findAll(
            @Param("form") form: ProblemForm,
            @Param("userId") userId: String?,
            @Param("lang") lang: String?,
            @Param("pageable") pageable: Pageable): List<Problem>

    fun count(@Param("form") form: ProblemForm, @Param("lang") lang: String?): Long

    fun touchI18n(
            @Param("problemId") problemId: Long,
            @Param("lang") lang: String): Long

    fun delete(@Param("id") id: Long): Long

    fun deleteI18n(@Param("id") id: Long): Long

}
