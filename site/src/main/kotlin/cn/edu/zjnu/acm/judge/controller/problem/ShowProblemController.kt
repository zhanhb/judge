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
package cn.edu.zjnu.acm.judge.controller.problem

import cn.edu.zjnu.acm.judge.domain.Problem
import cn.edu.zjnu.acm.judge.exception.BusinessCode
import cn.edu.zjnu.acm.judge.exception.BusinessException
import cn.edu.zjnu.acm.judge.mapper.ProblemMapper
import cn.edu.zjnu.acm.judge.service.LocaleService
import cn.edu.zjnu.acm.judge.service.SystemService
import java.util.Locale
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam

import org.springframework.http.MediaType.TEXT_HTML_VALUE
import org.springframework.web.bind.annotation.RequestMapping

/**
 * @author zhanhb
 */
@Controller
@RequestMapping(produces = [TEXT_HTML_VALUE])
class ShowProblemController(
        @Suppress("SpringJavaInjectionPointsAutowiringInspection") private val problemMapper: ProblemMapper,
        private val localeService: LocaleService,
        private val systemService: SystemService
) {

    private fun getProblem(problemId: Long, locale: Locale?): Problem {
        val problem = problemMapper.findOne(problemId, localeService.resolve(locale))
        if (problem != null) {
            val disabled = problem.disabled
            if (disabled == null || !disabled) {
                return problem
            }
        }
        throw BusinessException(BusinessCode.PROBLEM_NOT_FOUND, problemId)
    }

    @GetMapping("showproblem")
    fun showProblem(model: Model, @RequestParam("problem_id") problemId: Long, locale: Locale?): String {
        val problem = getProblem(problemId, locale)
        model.addAttribute("problem", problem)
        model.addAttribute("isSpecial", systemService.isSpecialJudge(problemId))
        val title1 = problemId.toString() + " -- " + problem.title
        val title2 = problem.title
        model.addAttribute("title1", title1)
        model.addAttribute("title2", title2)
        return "problems/view"
    }

}
