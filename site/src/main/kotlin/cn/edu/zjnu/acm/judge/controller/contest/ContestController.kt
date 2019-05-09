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
package cn.edu.zjnu.acm.judge.controller.contest

import cn.edu.zjnu.acm.judge.exception.BusinessCode
import cn.edu.zjnu.acm.judge.exception.BusinessException
import cn.edu.zjnu.acm.judge.service.ContestService
import com.google.common.collect.ImmutableMap
import org.springframework.http.MediaType.TEXT_HTML_VALUE
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.servlet.ModelAndView
import org.springframework.web.servlet.mvc.support.RedirectAttributes
import java.util.Locale
import java.util.concurrent.Future

/**
 * @author zhanhb
 */
@Controller("contest")
@RequestMapping(value = ["/contests/{contestId}"], produces = [TEXT_HTML_VALUE])
class ContestController(
        private val contestService: ContestService
) {

    @GetMapping("standing")
    fun standingHtml(@PathVariable("contestId") contestId: Long, locale: Locale?): Future<ModelAndView> {
        val contest = contestService.getContestAndProblemsNotDisabled(contestId, null, locale)
        if (!contest.isStarted) {
            throw BusinessException(BusinessCode.CONTEST_NOT_STARTED, contest.id ?: "", contest.startTime ?: "")
        }
        return contestService.standingAsync(contestId).thenApply { standing ->
            ModelAndView("contests/standing", ImmutableMap.builder<String, Any>()
                    .put("contestId", contestId)
                    .put("contest", contest)
                    .put("id", contestId)
                    .put("problems", contest.problems!!)
                    .put("standing", standing)
                    .build())
        }
    }

    @GetMapping
    fun index(@PathVariable("contestId") contestId: Long, redirectAttributes: RedirectAttributes): String {
        redirectAttributes.addAttribute("contestId", contestId)
        return "redirect:/contests/{contestId}/problems.html"
    }

}
