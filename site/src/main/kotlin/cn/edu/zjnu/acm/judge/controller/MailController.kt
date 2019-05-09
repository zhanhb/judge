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
package cn.edu.zjnu.acm.judge.controller

import cn.edu.zjnu.acm.judge.domain.Mail
import cn.edu.zjnu.acm.judge.exception.BusinessCode
import cn.edu.zjnu.acm.judge.exception.BusinessException
import cn.edu.zjnu.acm.judge.mapper.MailMapper
import cn.edu.zjnu.acm.judge.service.AccountService
import cn.edu.zjnu.acm.judge.service.impl.UserDetailsServiceImpl
import cn.edu.zjnu.acm.judge.util.JudgeUtils
import org.springframework.http.MediaType.TEXT_HTML_VALUE
import org.springframework.security.access.annotation.Secured
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

/**
 *
 * @author zhanhb
 */
@Controller
@RequestMapping(produces = [TEXT_HTML_VALUE])
@Secured("ROLE_USER")
class MailController(
        private val accountService: AccountService,
        @Suppress("SpringJavaInjectionPointsAutowiringInspection") private val mailMapper: MailMapper
) {

    private fun access(mailId: Long, authentication: Authentication): Mail {
        val mail = mailMapper.findOne(mailId) ?: throw BusinessException(BusinessCode.MAIL_NOT_FOUND)
        if (!UserDetailsServiceImpl.isUser(authentication, mail.to)) {
            throw BusinessException(BusinessCode.MAIL_INVALID_ACCESS)
        }
        return mail
    }

    @GetMapping("deletemail")
    fun delete(@RequestParam("mail_id") mailId: Long, authentication: Authentication): String {
        access(mailId, authentication)
        mailMapper.delete(mailId)
        return "redirect:/mail"
    }

    @GetMapping("mail")
    fun mail(model: Model,
             @RequestParam(value = "size", defaultValue = "20") size: Int,
             @RequestParam(value = "start", defaultValue = "1") start: Long,
             authentication: Authentication): String {
        var start = start
        if (start <= 0) {
            start = 1
        }
        val currentUserId = authentication.name

        val mails = mailMapper.findAllByTo(currentUserId!!, start - 1, size)

        model.addAttribute("userId", currentUserId)
        model.addAttribute("mails", mails)
        model.addAttribute("size", size)
        model.addAttribute("start", start)
        return "mails/list"
    }

    @PostMapping("send")
    fun send(@RequestParam("title") title: String,
             @RequestParam("to") to: String,
             @RequestParam("content") content: String,
             authentication: Authentication): String {
        var title = title
        val userId = authentication.name
        if (title.isNullOrBlank()) {
            title = "No Topic"
        }
        if (content.length > 40000) {
            throw BusinessException(BusinessCode.MAIL_CONTENT_LONG)
        }
        accountService.findOne(to)

        mailMapper.save(Mail(from = userId, to = to, title = title, content = content))
        return "mails/sendsuccess"
    }

    @GetMapping("sendpage", "send")
    fun sendPage(model: Model,
                 @RequestParam(value = "reply", defaultValue = "-1") reply: Long,
                 @RequestParam(value = "to", defaultValue = "") userId: String?,
                 authentication: Authentication): String {
        var userId = userId
        var title: String? = ""
        var content: String? = ""

        if (reply != -1L) {
            val mail = mailMapper.findOne(reply)
                    ?: throw BusinessException(BusinessCode.MAIL_NOT_FOUND)
            if (!UserDetailsServiceImpl.isUser(authentication, mail.to)) {
                throw BusinessException(BusinessCode.MAIL_INVALID_ACCESS)
            }
            userId = mail.from
            title = mail.title
            content = mail.content
            if (!title!!.startsWith("re:", ignoreCase = true)) {
                title = "Re:$title"
            }
            mailMapper.setReply(reply)
        }
        model.addAttribute("to", userId)
        model.addAttribute("title", title)
        model.addAttribute("content", JudgeUtils.getReplyString(content))
        return "mails/sendpage"
    }

    @GetMapping("showmail")
    fun showMail(model: Model,
                 @RequestParam("mail_id") mailId: Long,
                 authentication: Authentication): String {
        val mail = access(mailId, authentication)
        mailMapper.readed(mailId)
        model.addAttribute("mail", mail)
        return "mails/view"
    }

}
