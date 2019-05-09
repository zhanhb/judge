/*
 * Copyright 2017 ZJNU ACM.
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
package cn.edu.zjnu.acm.judge.controller.legacy

import org.springframework.http.MediaType.TEXT_HTML_VALUE
import org.springframework.security.access.annotation.Secured
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.servlet.mvc.support.RedirectAttributes

/**
 *
 * @author zhanhb
 */
@Controller
@Deprecated("")
@RequestMapping(produces = [TEXT_HTML_VALUE])
@Secured("ROLE_ADMIN")
class LegacyAdminController {

    @Deprecated("")
    @GetMapping("admin.showproblem")
    fun showProblem(@RequestParam("problem_id") problemId: Long,
                    redirectAttributes: RedirectAttributes): String {
        redirectAttributes.addAttribute("problemId", problemId)
        return "redirect:/admin/problems/{problemId}.html"
    }

    @Deprecated("")
    @GetMapping("admin.showcontest")
    fun showContest(@RequestParam("contest_id") contestId: Long,
                    redirectAttributes: RedirectAttributes): String {
        redirectAttributes.addAttribute("contestId", contestId)
        return "redirect:/admin/contests/{contestId}.html"
    }

    @Deprecated("")
    @GetMapping("admin.rejudge")
    fun rejudge(attributes: RedirectAttributes, @RequestParam query: Map<String, String>): String {
        attributes.addAllAttributes(query)
        return "redirect:/admin/rejudge"
    }

}
