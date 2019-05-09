/*
 * Copyright 2018 ZJNU ACM.
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
package cn.edu.zjnu.acm.judge.service.impl

import cn.edu.zjnu.acm.judge.mapper.EmailMapper
import cn.edu.zjnu.acm.judge.service.EmailService
import org.springframework.mail.javamail.JavaMailSenderImpl
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Service
import javax.mail.MessagingException

/**
 *
 * @author zhanhb
 */
@Service("emailService")
class EmailServiceImpl(
        @Suppress("SpringJavaInjectionPointsAutowiringInspection") private val emailMapper: EmailMapper,
        @Suppress("SpringJavaInjectionPointsAutowiringInspection") private val javaMailSender: JavaMailSenderImpl
) : EmailService {

    @Throws(MessagingException::class)
    override fun send(to: String, subject: String, content: String) {
        emailMapper.save(to, subject, content)
        val mimeMessage = javaMailSender.createMimeMessage()

        val helper = MimeMessageHelper(mimeMessage, true)
        helper.setTo(to)
        helper.setSubject(subject)
        helper.setText(content, true)
        helper.setFrom(javaMailSender.username!!)

        javaMailSender.send(mimeMessage)
    }

}
