/*
 * Copyright 2014 zhanhb.
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
package cn.edu.zjnu.acm.judge.controller.user

import cn.edu.zjnu.acm.judge.domain.User
import cn.edu.zjnu.acm.judge.mapper.UserMapper
import cn.edu.zjnu.acm.judge.service.EmailService
import cn.edu.zjnu.acm.judge.service.ResetPasswordService
import cn.edu.zjnu.acm.judge.service.SystemService
import cn.edu.zjnu.acm.judge.util.ValueCheck
import org.slf4j.LoggerFactory
import org.springframework.http.MediaType.TEXT_HTML_VALUE
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.thymeleaf.TemplateEngine
import org.thymeleaf.context.Context
import java.io.IOException
import java.util.*
import javax.mail.MessagingException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * @author zhanhb
 */
@Controller
class ResetPasswordController(
        private val emailService: EmailService,
        private val templateEngine: TemplateEngine,

        @Suppress("SpringJavaInjectionPointsAutowiringInspection") private val userMapper: UserMapper,
        private val passwordEncoder: PasswordEncoder,
        private val systemService: SystemService,
        private val resetPasswordService: ResetPasswordService
) {
    @GetMapping(value = ["/resetPassword"], produces = [TEXT_HTML_VALUE])
    fun doGet(
            @RequestParam(value = "u", required = false) userId: String?,
            @RequestParam(value = "vc", required = false) vcode: String?): String {
        return if (resetPasswordService.checkVcode(userId, vcode) != null) "users/resetPassword"
        else "users/resetPasswordInvalid"
    }

    @PostMapping("/resetPassword")
    @Throws(IOException::class)
    fun doPost(request: HttpServletRequest, response: HttpServletResponse,
               @RequestParam(value = "verify", required = false) verify: String?,
               @RequestParam(value = "username", required = false) username: String?,
               locale: Locale?) {
        response.contentType = "text/javascript;charset=UTF-8"
        val out = response.writer

        val session = request.getSession(false)
        var word: String? = null
        if (session != null) {
            word = session.getAttribute(com.google.code.kaptcha.Constants.KAPTCHA_SESSION_KEY) as String?
            session.removeAttribute(com.google.code.kaptcha.Constants.KAPTCHA_SESSION_KEY)
        }
        if (word == null || !word.equals(verify, ignoreCase = true)) {
            out.print("alert('验证码错误');")
            return
        }

        val user = if (username != null) userMapper.findOne(username) else null
        if (user == null) {
            out.print("alert('用户不存在');")
            return
        }
        val email = user.email
        if (email == null || !email.toLowerCase().matches(ValueCheck.EMAIL_PATTERN.toRegex())) {
            out.print("alert('该用户未设置邮箱或邮箱格式不对，如需重置密码，请联系管理员！');")
            return
        }
        try {
            val vc = resetPasswordService.getOrCreate(user.id)
            val url = getPath(request, "/resetPassword.html?vc=", "$vc&u=", user.id!!)
            val title = systemService.resetPasswordTitle?: ""
            val map = mapOf("url" to url, "title" to title)

            val content = templateEngine.process("users/password", Context(locale, map))

            emailService.send(email, title, content)
        } catch (ex: MessagingException) {
            log.error("fail to send email", ex)
            out.print("alert('邮件发送失败，请稍后再试')")
            return
        } catch (ex: RuntimeException) {
            log.error("fail to send email", ex)
            out.print("alert('邮件发送失败，请稍后再试')")
            return
        } catch (ex: Error) {
            log.error("fail to send email", ex)
            out.print("alert('邮件发送失败，请稍后再试')")
            return
        }

        out.print("alert('已经将邮件发送到" + user.email + "，请点击链接重设密码');")
    }

    @PostMapping(value = ["/resetPassword"], params = ["action=changePassword"])
    @Throws(IOException::class)
    fun changePassword(request: HttpServletRequest, response: HttpServletResponse,
                       @RequestParam(value = "u", required = false) userId: String?,
                       @RequestParam(value = "vc", required = false) vcode: String?) {
        response.contentType = "text/javascript;charset=UTF-8"
        val out = response.writer
        val user = resetPasswordService.checkVcode(userId, vcode)
        if (user == null) {
            out.print("alert(\"效链接已失效，请重新获取链接\");")
            return
        }
        val newPassword = request.getParameter("newPassword")
        ValueCheck.checkPassword(newPassword)
        userMapper.updateSelective(user.id!!, User(password = passwordEncoder.encode(newPassword)))
        resetPasswordService.remove(userId)
        out.print("alert(\"密码修改成功！\");")
        out.print("document.location='" + request.contextPath + "'")
    }

    private fun getPath(request: HttpServletRequest, vararg params: String): String {
        val serverPort = request.serverPort
        val defaultPort = if (request.isSecure) 443 else 80
        val sb = StringBuilder(80)

        sb.append(request.scheme).append("://").append(request.serverName)
        if (serverPort != defaultPort) {
            sb.append(":").append(serverPort)
        }
        sb.append(request.contextPath)
        for (param in params) {
            sb.append(param)
        }
        return sb.toString()
    }

    companion object {
        private val log = LoggerFactory.getLogger(ResetPasswordController::class.java)
    }
}
