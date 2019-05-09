package cn.edu.zjnu.acm.judge.controller.submission

import cn.edu.zjnu.acm.judge.mapper.UserPreferenceMapper
import cn.edu.zjnu.acm.judge.service.ContestOnlyService
import cn.edu.zjnu.acm.judge.service.LanguageService
import cn.edu.zjnu.acm.judge.service.SubmissionService
import org.springframework.http.MediaType
import javax.servlet.http.HttpServletRequest
import org.springframework.security.access.annotation.Secured
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.mvc.support.RedirectAttributes

/**
 * @author zhanhb
 */
@Controller
@RequestMapping(produces = [MediaType.TEXT_HTML_VALUE])
@Secured("ROLE_USER")
class SubmitController(
        private val contestOnlyService: ContestOnlyService,
        private val submissionService: SubmissionService,
        private val userPerferenceMapper: UserPreferenceMapper,
        private val languageService: LanguageService
) {

    @GetMapping("submitpage", "submit")
    fun submitPage(model: Model,
                   @RequestParam(value = "problem_id", required = false) problemId: Long?,
                   authentication: Authentication): String {
        return page(null, problemId, authentication, model)
    }

    @GetMapping("problems/{problemId}/submit", "contests/{contestId}/problems/{problemId}/submit")
    fun contestSubmitPage(model: Model,
                          @PathVariable(value = "contestId", required = false) contestId: Long?,
                          @PathVariable("problemId") problemId: Long,
                          authentication: Authentication): String {
        return page(contestId, problemId, authentication, model)
    }

    private fun page(contestId: Long?, problemId: Long?, authentication: Authentication?, model: Model): String {
        model.addAttribute("contestId", contestId)
        model.addAttribute("problemId", problemId)
        model.addAttribute("languages", languageService.availableLanguages.values)
        val user = authentication?.name
        val languageId = userPerferenceMapper.getLanguage(user!!)
        model.addAttribute("languageId", languageId)
        return "submissions/index"
    }

    @PostMapping("problems/{problemId}/submit", "contests/{contestId}/problems/{problemId}/submit")
    @Synchronized
    fun submit(request: HttpServletRequest,
               @PathVariable(value = "contestId", required = false) contestId: Long?,
               @PathVariable("problemId") problemId: Long,
               @RequestParam("language") languageId: Int,
               @RequestParam("source") source: String,
               redirectAttributes: RedirectAttributes,
               authentication: Authentication?): String {
        contestOnlyService.checkSubmit(request, contestId, problemId)
        val userId = authentication!!.name
        val ip = request.remoteAddr
        //提交是否在竞赛中
        redirectAttributes.addAttribute("user_id", userId)
        if (contestId == null) {
            submissionService.submit(languageId, source, userId, ip, problemId, true)
        } else {
            // TODO if contest is ended, should redirect to stats rather than contest's status
            redirectAttributes.addAttribute("contest_id", contestId)
            submissionService.contestSubmit(languageId, source, userId, ip, contestId, problemId)
        }
        return "redirect:/status"
    }

}
