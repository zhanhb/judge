/*
 * Copyright 2017-2019 ZJNU ACM.
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

import cn.edu.zjnu.acm.judge.data.form.ProblemForm
import cn.edu.zjnu.acm.judge.domain.Problem
import cn.edu.zjnu.acm.judge.exception.BusinessCode
import cn.edu.zjnu.acm.judge.exception.BusinessException
import cn.edu.zjnu.acm.judge.mapper.ProblemMapper
import cn.edu.zjnu.acm.judge.mapper.UserProblemMapper
import cn.edu.zjnu.acm.judge.service.ContestService
import cn.edu.zjnu.acm.judge.service.LocaleService
import cn.edu.zjnu.acm.judge.service.ProblemService
import cn.edu.zjnu.acm.judge.service.SystemService
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.time.Instant
import java.util.Locale
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 *
 * @author zhanhb
 */
@Service("problemService")
class ProblemServiceImpl(
        @Suppress("SpringJavaInjectionPointsAutowiringInspection") private val problemMapper: ProblemMapper,
        private val localeService: LocaleService,
        private val contestService: ContestService,
        private val systemService: SystemService,
        @Suppress("SpringJavaInjectionPointsAutowiringInspection") private val userProblemMapper: UserProblemMapper
) : ProblemService {
    override fun findAll(problemForm: ProblemForm, userId: String?, pageable: Pageable, locale: Locale?): Page<Problem> {
        val resolve = localeService.resolve(locale)
        val total = problemMapper.count(problemForm, resolve)
        return PageImpl(problemMapper.findAll(problemForm, userId, resolve, pageable), pageable, total)
    }

    @Transactional
    override fun updateSelective(problemId: Long, p: Problem, requestLocale: String?) {
        problemMapper.findOne(problemId, requestLocale)
                ?: throw BusinessException(BusinessCode.PROBLEM_NOT_FOUND, problemId)
        val locale = convert(requestLocale)
        if (locale != null) {
            problemMapper.touchI18n(problemId, locale)
        }
        problemMapper.updateSelective(problemId, p.copy(createdTime = null, modifiedTime = Instant.now()), locale)
    }

    @Transactional
    override fun save(problem: Problem): Problem {
        var disabled = true
        val contests = problem.contests
        if (contests != null) {
            for (contest in contests) {
                if (contest == 0L) {
                    disabled = false
                }
            }
        }
        // TODO The following two lines both change the request content
        problem.disabled = disabled
        problemMapper.save(problem)
        val id = problem.id!!
        if (contests != null) {
            for (contest in contests) {
                if (contest != 0L) {
                    contestService.addProblem(contest, id)
                }
            }
        }
        try {
            Files.createDirectories(getDataDirectory(id))
        } catch (ex: IOException) {
        }

        return problem
    }

    private fun convert(lang: String?): String? {
        return if (lang.isNullOrEmpty()) null else localeService.findOne(lang)?.id
    }

    override fun findOne(id: Long, lang: String?): Problem {
        return problemMapper.findOne(id, convert(lang)) ?: throw BusinessException(BusinessCode.PROBLEM_NOT_FOUND, id)
    }

    override fun findOne(id: Long): Problem {
        return problemMapper.findOne(id, null) ?: throw BusinessException(BusinessCode.PROBLEM_NOT_FOUND, id)
    }

    override fun findOneNoI18n(id: Long): Problem {
        return problemMapper.findOneNoI18n(id) ?: throw BusinessException(BusinessCode.PROBLEM_NOT_FOUND, id)
    }

    override fun getDataDirectory(id: Long): Path {
        return systemService.getDataDirectory(id)
    }

    @Transactional
    override fun delete(id: Long) {
        val total = (problemMapper.deleteI18n(id)
                + userProblemMapper.deleteByProblem(id)
                + problemMapper.delete(id))
        if (total == 0L) {
            throw BusinessException(BusinessCode.PROBLEM_NOT_FOUND, id)
        }
    }

}
