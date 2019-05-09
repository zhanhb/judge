package cn.edu.zjnu.acm.judge.controller.user

import cn.edu.zjnu.acm.judge.domain.User
import cn.edu.zjnu.acm.judge.exception.BusinessCode
import cn.edu.zjnu.acm.judge.exception.BusinessException
import cn.edu.zjnu.acm.judge.mapper.UserMapper
import cn.edu.zjnu.acm.judge.service.ContestOnlyService
import cn.edu.zjnu.acm.judge.util.ValueCheck
import org.slf4j.LoggerFactory
import org.springframework.http.MediaType
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import javax.servlet.http.HttpServletRequest

/**
 * @author zhanhb
 */
@Controller
@RequestMapping(produces = [MediaType.TEXT_HTML_VALUE])
class RegisterController(
        private val passwordEncoder: PasswordEncoder,
        @Suppress("SpringJavaInjectionPointsAutowiringInspection") private val userMapper: UserMapper,
        private val contestOnlyService: ContestOnlyService
) {
    @PostMapping("register")
    fun register(
            request: HttpServletRequest,
            @RequestParam("user_id") userId: String,
            @RequestParam("school") school: String,
            @RequestParam("nick") nick: String,
            @RequestParam("password") password: String,
            @RequestParam("email") email: String,
            @RequestParam("rptPassword") rptPassword: String): String {
        var nick = nick
        contestOnlyService.checkRegister()
        ValueCheck.checkUserId(userId)
        ValueCheck.checkPassword(password)
        ValueCheck.checkEmail(email)
        if (password != rptPassword) {
            throw BusinessException(BusinessCode.PASSWORD_NOT_MATCH)
        }
        if (nick.isBlank()) {
            nick = userId
        } else {
            nick = nick.trim()
            ValueCheck.checkNick(nick)
        }
        if (userMapper.findOne(userId) != null) {
            throw BusinessException(BusinessCode.IMPORT_USER_EXISTS, userId)
        }
        val user = User(
                id = userId,
                password = passwordEncoder.encode(password),
                email = if (email.isBlank()) null else email,
                nick = nick,
                school = school,
                ip = request.remoteAddr
        )
        userMapper.save(user)
        log.info("{}", user)
        request.setAttribute("user", user)
        return "redirect:/login"
    }

    companion object {
        private val log = LoggerFactory.getLogger(RegisterController::class.java)
    }
}
