package cn.edu.zjnu.acm.judge.controller.user

import cn.edu.zjnu.acm.judge.domain.User
import cn.edu.zjnu.acm.judge.exception.BusinessCode
import cn.edu.zjnu.acm.judge.exception.BusinessException
import cn.edu.zjnu.acm.judge.service.AccountService
import cn.edu.zjnu.acm.judge.util.ValueCheck
import org.springframework.http.MediaType
import org.springframework.security.access.annotation.Secured
import org.springframework.security.core.Authentication
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.util.StringUtils
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

/**
 * @author zhanhb
 */
@Controller
@RequestMapping(produces = [MediaType.TEXT_HTML_VALUE])
@Secured("ROLE_USER")
class ModifyUserController(
        private val accountService: AccountService,
        private val passwordEncoder: PasswordEncoder
) {

    @GetMapping("modifyuserpage", "modifyuser")
    fun updatePage(model: Model, authentication: Authentication?): String {
        val userId = authentication?.name
        val user = accountService.findOne(userId!!)
        model.addAttribute("user", user)
        return "users/edit"
    }

    @PostMapping("modifyuser")
    fun update(model: Model,
               @RequestParam("oldPassword") oldPassword: String,
               @RequestParam("newPassword") newPassword: String,
               @RequestParam("rptPassword") rptPassword: String,
               @RequestParam("email") email: String,
               @RequestParam("nick") nick: String,
               @RequestParam("school") school: String,
               authentication: Authentication): String {
        var newPassword = newPassword
        var email = email
        var nick = nick
        if (newPassword != rptPassword) {
            throw BusinessException(BusinessCode.PASSWORD_NOT_MATCH)
        }
        val userId = authentication.name
        var user = accountService.findOne(userId)
        val password = user.password
        if (!passwordEncoder.matches(oldPassword, password)) {
            throw BusinessException(BusinessCode.PASSWORD_NOT_CORRECT)
        }
        if (StringUtils.isEmpty(nick)) {
            nick = userId
        }
        if (StringUtils.isEmpty(newPassword)) {
            newPassword = oldPassword
        } else {
            ValueCheck.checkPassword(newPassword)
        }
        if (StringUtils.hasText(email)) {
            ValueCheck.checkEmail(email)
        } else {
            email = ""
        }
        ValueCheck.checkNick(nick)
        user = User(
                id = userId,
                email = email,
                nick = nick,
                password = newPassword,
                school = school
        )
        accountService.updateSelective(userId, user)
        model.addAttribute("user", user)
        return "users/modifySuccess"
    }

}
