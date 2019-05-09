package cn.edu.zjnu.acm.judge.controller

import cn.edu.zjnu.acm.judge.Application
import cn.edu.zjnu.acm.judge.domain.Mail
import cn.edu.zjnu.acm.judge.mapper.MailMapper
import cn.edu.zjnu.acm.judge.service.MockDataService
import org.junit.jupiter.api.Test
import org.junit.platform.runner.JUnitPlatform
import org.junit.runner.RunWith
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user
import org.springframework.test.context.web.WebAppConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.transaction.annotation.Transactional

@AutoConfigureMockMvc
@RunWith(JUnitPlatform::class)
@SpringBootTest(classes = [Application::class])
@Transactional
@WebAppConfiguration
class MailControllerTest {

    @Autowired
    private val mvc: MockMvc? = null
    @Autowired
    private val mockDataService: MockDataService? = null
    @Suppress("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private val mailMapper: MailMapper? = null

    /**
     * Test of delete method, of class MailController.
     *
     * [MailController.delete]
     */
    @Test
    @Throws(Exception::class)
    fun testDelete() {
        log.info("delete")
        val userId = mockDataService!!.user().id!!
        val mailId = newMail(mockDataService.user().id!!, userId).id
        val result = mvc!!.perform(get("/deletemail").with(user(userId))
                .param("mail_id", java.lang.Long.toString(mailId)))
                .andExpect(status().isFound)
                .andExpect(redirectedUrl("/mail"))
                .andReturn()
    }

    /**
     * Test of mail method, of class MailController.
     *
     * [MailController.mail]
     */
    @Test
    @Throws(Exception::class)
    fun testMail() {
        log.info("mail")
        val size = 20
        val start: Long = 0
        val userId = mockDataService!!.user().id
        val result = mvc!!.perform(get("/mail").with(user(userId))
                .param("size", Integer.toString(size))
                .param("start", java.lang.Long.toString(start)))
                .andExpect(status().isOk)
                .andExpect(view().name("mails/list"))
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andReturn()
    }

    /**
     * Test of send method, of class MailController.
     *
     * [MailController.send]
     */
    @Test
    @Throws(Exception::class)
    fun testSend() {
        log.info("send")
        val userId = mockDataService!!.user().id
        val title = "title"
        val to = mockDataService!!.user().id
        val content = "content"
        val result = mvc!!.perform(post("/send").with(user(userId))
                .param("title", title)
                .param("to", to)
                .param("content", content))
                .andExpect(status().isOk)
                .andExpect(view().name("mails/sendsuccess"))
                .andReturn()
    }

    /**
     * Test of sendPage method, of class MailController.
     *
     * [MailController.sendPage]
     */
    @Test
    @Throws(Exception::class)
    fun testSendPage() {
        log.info("sendPage")
        val reply: Long = -1
        val to = mockDataService!!.user().id
        val userId = mockDataService.user().id
        val result = mvc!!.perform(get("/sendpage").with(user(userId))
                .param("reply", java.lang.Long.toString(reply))
                .param("to", to))
                .andExpect(status().isOk)
                .andExpect(view().name("mails/sendpage"))
                .andReturn()
    }

    /**
     * Test of showMail method, of class MailController.
     *
     * [MailController.showMail]
     */
    @Test
    @Throws(Exception::class)
    fun testShowMail() {
        log.info("showMail")
        val userId = mockDataService!!.user().id!!
        val mailId = newMail(mockDataService.user().id!!, userId).id
        val result = mvc!!.perform(get("/showmail").with(user(userId))
                .param("mail_id", java.lang.Long.toString(mailId)))
                .andExpect(status().isOk)
                .andExpect(view().name("mails/view"))
                .andReturn()
    }

    private fun newMail(from: String, to: String): Mail {
        val mail = Mail(from = from, to = to, title = "title5", content = "content12")
        mailMapper!!.save(mail)
        return mail
    }

    companion object {
        private val log = LoggerFactory.getLogger(MailControllerTest::class.java)
    }
}
