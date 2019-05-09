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
package cn.edu.zjnu.acm.judge.controller.contest

import cn.edu.zjnu.acm.judge.exception.BusinessCode
import cn.edu.zjnu.acm.judge.exception.BusinessException
import cn.edu.zjnu.acm.judge.mapper.SubmissionMapper
import cn.edu.zjnu.acm.judge.service.ContestService
import cn.edu.zjnu.acm.judge.service.SubmissionService
import cn.edu.zjnu.acm.judge.service.SystemService
import cn.edu.zjnu.acm.judge.service.impl.UserDetailsServiceImpl
import cn.edu.zjnu.acm.judge.util.ResultType
import cn.edu.zjnu.acm.judge.util.URIBuilder
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.http.MediaType.TEXT_HTML_VALUE
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import java.util.*
import javax.servlet.http.HttpServletRequest
import kotlin.collections.ArrayList

/**
 * @author zhanhb
 */
@Controller
@RequestMapping(value = ["contests/{contestId}/problems"], produces = [TEXT_HTML_VALUE])
class ContestProblemController(
        private val systemService: SystemService,
        private val contestService: ContestService,
        @Suppress("SpringJavaInjectionPointsAutowiringInspection") private val submissionMapper: SubmissionMapper,
        private val submissionService: SubmissionService
) {

    @GetMapping
    fun problems(model: Model, locale: Locale?,
                 @PathVariable("contestId") contestId: Long,
                 authentication: Authentication?): String {
        val contest = contestService.getContestAndProblemsNotDisabled(contestId, authentication?.name, locale)
        model.addAttribute("contestId", contestId)
        model.addAttribute("contest", contest)
        if (contest.isStarted) {
            model.addAttribute("problems", contest.problems)
        } else {
            contest.problems = null
        }
        return "contests/problems"
    }

    @GetMapping("{pid}")
    fun showProblem(@PathVariable("contestId") contestId: Long, @PathVariable("pid") problemNum: Long, model: Model, locale: Locale?): String {
        val contest = contestService.getContestAndProblemsNotDisabled(contestId, null, locale)
        if (!contest.isStarted) {
            throw BusinessException(BusinessCode.CONTEST_NOT_STARTED, contest.id ?: "", contest.startTime ?: "")
        }
        val problem = contestService.getProblem(contestId, problemNum, locale)
        model.addAttribute("problem", problem)
        model.addAttribute("problems", contest.problems)
        model.addAttribute("isSpecial", systemService.isSpecialJudge(problem.origin!!))
        val problemsId = contest.problems!!.map({ it.origin })
        val index = contestService.toProblemIndex(problemsId.indexOf(problem.origin).toLong())
        val title1 = index + ":" + problem.origin + " -- " + problem.title
        val title2 = index + ":" + problem.title
        model.addAttribute("title1", title1)
        model.addAttribute("title2", title2)
        return "contests/problem"
    }

    @GetMapping("{pid}/status")
    fun status(
            @PathVariable("contestId") contestId: Long,
            @PathVariable("pid") problemNum: Int,
            @PageableDefault(size = 20, sort = ["time", "memory", "code_length"]) pageable: Pageable,
            model: Model, authentication: Authentication?, request: HttpServletRequest): String {
        contestService.findOneByIdAndNotDisabled(contestId) // check if problem exists and not disabled
        val problem = contestService.getProblem(contestId, problemNum.toLong(), null)
        val page = submissionService.bestSubmission(contestId, problem.origin!!, pageable, problem.submitUser!!)
        model.addAttribute("page", page)
        val list = submissionMapper.groupByScore(contestId, problem.origin!!)
        val scores = ArrayList<String>(list.size)
        val counts = ArrayList<Long>(list.size)
        val urls = ArrayList<String>(list.size)
        for ((score, count) in list) {
            scores.add(ResultType.getShowsourceString(score))
            counts.add(count)
            urls.add(request.contextPath + "/status?contest_id=" + contestId + "&problem_id=" + problem.origin + "&score=" + score)
        }
        val isAdmin = UserDetailsServiceImpl.isAdminLoginned(request)
        val isSourceBrowser = UserDetailsServiceImpl.isSourceBrowser(request)
        val canView = isAdmin || isSourceBrowser

        request.setAttribute("page", page)
        request.setAttribute("sa", listOf(counts, scores, urls))
        request.setAttribute("problem", problem)
        request.setAttribute("url", URIBuilder.fromRequest(request).replaceQueryParam("page").toString())
        request.setAttribute("contestId", contestId)
        request.setAttribute("canView", canView)
        request.setAttribute("authentication", authentication)
        return "contests/problems-status"
    }

}
