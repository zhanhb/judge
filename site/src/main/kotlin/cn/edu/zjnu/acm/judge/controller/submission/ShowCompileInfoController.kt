package cn.edu.zjnu.acm.judge.controller.submission

import cn.edu.zjnu.acm.judge.service.SubmissionService
import org.springframework.http.MediaType.TEXT_HTML_VALUE
import org.springframework.security.access.annotation.Secured
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

/**
 * @author zhanhb
 */
@Controller
@RequestMapping(produces = [TEXT_HTML_VALUE])
class ShowCompileInfoController(
        private val submissionService: SubmissionService
) {
    @GetMapping("showcompileinfo")
    @Secured("ROLE_USER")
    fun showCompileInfo(@RequestParam("solution_id") submissionId: Long,
                        model: Model, authentication: Authentication): String {
        val compileInfo = submissionService.findCompileInfo(submissionId)
        model.addAttribute("compileInfo", compileInfo)
        return "submissions/compile_info"
    }
}
