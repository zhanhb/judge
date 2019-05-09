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
package cn.edu.zjnu.acm.judge.controller.problem

import cn.edu.zjnu.acm.judge.mapper.SubmissionMapper
import cn.edu.zjnu.acm.judge.service.ProblemService
import cn.edu.zjnu.acm.judge.service.SubmissionService
import cn.edu.zjnu.acm.judge.service.impl.UserDetailsServiceImpl
import cn.edu.zjnu.acm.judge.util.ResultType
import cn.edu.zjnu.acm.judge.util.URIBuilder
import org.slf4j.LoggerFactory
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.http.MediaType.TEXT_HTML_VALUE
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.servlet.mvc.support.RedirectAttributes
import javax.servlet.http.HttpServletRequest

/**
 *
 * @author zhanhb
 */
@Controller
@RequestMapping(produces = [TEXT_HTML_VALUE])
class ProblemStatusController(
        private val problemService: ProblemService,
        private val submissionService: SubmissionService,
        @Suppress("SpringJavaInjectionPointsAutowiringInspection") private val submissionMapper: SubmissionMapper
) {

    @GetMapping("gotoproblem")
    fun gotoProblem(@RequestParam(value = "pid", required = false) pid: String,
                    redirectAttributes: RedirectAttributes): String {
        val problemId: Long
        try {
            problemId = java.lang.Long.parseLong(pid)
        } catch (ex: NumberFormatException) {
            redirectAttributes.addAttribute("sstr", pid)
            return "redirect:/searchproblem"
        }

        redirectAttributes.addAttribute("problem_id", problemId)
        return "redirect:/showproblem"
    }

    @GetMapping("problemstatus")
    fun status(request: HttpServletRequest,
               @RequestParam("problem_id") id: Long,
               @PageableDefault(size = 20, sort = ["time", "memory", "code_length"]) pageable: Pageable,
               authentication: Authentication?): String {
        var pageable = pageable
        log.debug("{}", pageable)
        if (pageable.pageSize > 500) {
            pageable = PageRequest.of(pageable.pageNumber, 500, pageable.sort)
        }
        val problem = problemService.findOneNoI18n(id)
        val list = submissionMapper.groupByScore(null, id)
        val scores = ArrayList<String>(list.size)
        val counts = ArrayList<Long>(list.size)
        val urls = ArrayList<String>(list.size)
        for ((score, count) in list) {
            scores.add(ResultType.getShowsourceString(score))
            counts.add(count)
            urls.add(request.contextPath + "/status?problem_id=" + id + "&score=" + score)
        }
        val page = submissionService.bestSubmission(null, id, pageable, problem.submitUser!!)
        val isAdmin = UserDetailsServiceImpl.isAdminLoginned(request)
        val isSourceBrowser = UserDetailsServiceImpl.isSourceBrowser(request)
        val canView = isAdmin || isSourceBrowser

        request.setAttribute("page", page)
        request.setAttribute("sa", listOf(counts, scores, urls))
        request.setAttribute("problem", problem)
        request.setAttribute("url", URIBuilder.fromRequest(request).replaceQueryParam("page").toString())
        request.setAttribute("canView", canView)
        request.setAttribute("authentication", authentication)

        return "problems/status"
    }

    companion object {
        private val log = LoggerFactory.getLogger(ProblemStatusController::class.java)
    }
}
