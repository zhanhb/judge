package cn.edu.zjnu.acm.judge.controller.user

import org.springframework.http.MediaType
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

/**
 * @author zhanhb
 */
@Controller
@RequestMapping(produces = [MediaType.TEXT_HTML_VALUE])
class LoginController {

    @GetMapping("loginpage", "login")
    fun login(model: Model,
              @RequestParam(value = "url", required = false) back: String?,
              @RequestHeader(value = "Referer", required = false) referrer: String?,
              @RequestParam(value = "contest_id", required = false) contestId: String?): String {
        model.addAttribute("backURL", if (!back.isNullOrBlank()) back else referrer)
        model.addAttribute("contestId", contestId)
        return "login"
    }

}
