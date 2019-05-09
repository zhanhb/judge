package cn.edu.zjnu.acm.judge.controller.user

import cn.edu.zjnu.acm.judge.Application
import cn.edu.zjnu.acm.judge.service.MockDataService
import com.google.code.kaptcha.Constants
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import org.junit.platform.runner.JUnitPlatform
import org.junit.runner.RunWith
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.mock.web.MockHttpSession
import org.springframework.test.context.web.WebAppConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.transaction.annotation.Transactional
import java.util.*

@AutoConfigureMockMvc
@RunWith(JUnitPlatform::class)
@SpringBootTest(classes = [Application::class])
@Transactional
@WebAppConfiguration
class ResetPasswordControllerTest {

    @Autowired
    private val mvc: MockMvc? = null
    @Autowired
    private val mockDataService: MockDataService? = null

    /**
     * Test of doGet method, of class ResetPasswordController.
     * [ResetPasswordController.doGet]
     */
    @Test
    @Throws(Exception::class)
    fun testDoGet() {
        log.info("doGet")
        val result = mvc!!.perform(get("/resetPassword"))
                .andExpect(status().isOk)
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andReturn()
    }

    /**
     * Test of methods doPost, changePassword, of class ResetPasswordController.
     *
     * [ResetPasswordController.doPost]
     * [ResetPasswordController.changePassword]
     */
    @Test
    @Throws(Exception::class)
    fun testChangePassword() {
        val locale = Locale.getDefault()
        val result = mvc!!.perform(get("/images/rand.jpg"))
                .andExpect(content().contentTypeCompatibleWith(MediaType.IMAGE_JPEG))
                .andReturn()
        val session = result.request.getSession(false)
        assertThat(session).isNotNull
        var verify = session!!.getAttribute(Constants.KAPTCHA_SESSION_KEY) as String
        val username = mockDataService!!.user(
                { user -> user.copy(email="admin@local.host") }
        ).id
        mvc.perform(post("/resetPassword")
                .param("verify", verify + "1")
                .param("username", username)
                .locale(locale))
                .andExpect(status().isOk)
                .andReturn()
        mvc.perform(post("/resetPassword")
                .session((session as MockHttpSession?)!!)
                .param("verify", verify + "1")
                .param("username", username)
                .locale(locale))
                .andExpect(status().isOk)
                .andReturn()
        assertThat(session.getAttribute(Constants.KAPTCHA_SESSION_KEY)).isNull()

        mvc.perform(get("/images/rand.jpg")
                .session((session as MockHttpSession?)!!))
                .andExpect(content().contentTypeCompatibleWith(MediaType.IMAGE_JPEG))
                .andReturn()
        verify = session.getAttribute(Constants.KAPTCHA_SESSION_KEY) as String
        mvc.perform(post("/resetPassword")
                .session((session as MockHttpSession?)!!)
                .param("verify", verify)
                .param("username", username)
                .locale(locale))
                .andExpect(status().isOk)
                .andReturn()
        assertNull(session.getAttribute(Constants.KAPTCHA_SESSION_KEY))
        mvc.perform(post("/resetPassword")
                .param("action", "changePassword"))
                .andExpect(status().isOk)
                .andReturn()
    }

    companion object {
        private val log = LoggerFactory.getLogger(ResetPasswordControllerTest::class.java)
    }
}
