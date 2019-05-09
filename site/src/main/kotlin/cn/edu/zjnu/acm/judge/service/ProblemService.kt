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

import cn.edu.zjnu.acm.judge.data.form.ProblemForm
import cn.edu.zjnu.acm.judge.domain.Problem
import java.nio.file.Path
import java.util.Locale

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

/**
 *
 * @author zhanhb
 */
interface ProblemService {

    fun delete(id: Long)

    fun findAll(problemForm: ProblemForm, userId: String?, pageable: Pageable, locale: Locale?): Page<Problem>

    fun findOne(id: Long, lang: String?): Problem

    fun findOne(id: Long): Problem

    fun findOneNoI18n(id: Long): Problem

    fun getDataDirectory(id: Long): Path

    fun save(problem: Problem): Problem

    fun updateSelective(problemId: Long, p: Problem, requestLocale: String?)

}
