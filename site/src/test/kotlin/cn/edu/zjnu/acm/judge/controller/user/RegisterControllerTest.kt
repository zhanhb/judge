package cn.edu.zjnu.acm.judge.controller.user

import cn.edu.zjnu.acm.judge.Application
import cn.edu.zjnu.acm.judge.service.MockDataService
import org.junit.jupiter.api.Test
import org.junit.platform.runner.JUnitPlatform
import org.junit.runner.RunWith
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.web.WebAppConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.transaction.annotation.Transactional

@AutoConfigureMockMvc
@RunWith(JUnitPlatform::class)
@SpringBootTest(classes = [Application::class])
@Transactional
@WebAppConfiguration
class RegisterControllerTest {

    @Autowired
    private val mvc: MockMvc? = null
    @Autowired
    private val mockDataService: MockDataService? = null

    /**
     * Test of register method, of class RegisterController.
     *
     * [RegisterController.register]
     */
    @Test
    @Throws(Exception::class)
    fun testRegister() {
        log.info("register")
        val userId = mockDataService!!.user(false).id
        val school = ""
        val nick = ""
        val email = ""
        val result = mvc!!.perform(post("/register")
                .param("user_id", userId)
                .param("school", school)
                .param("nick", nick)
                .param("password", userId)
                .param("email", email)
                .param("rptPassword", userId))
                .andExpect(status().isFound)
                .andExpect(redirectedUrl("/login"))
                .andReturn()
    }

    companion object {
        private val log = LoggerFactory.getLogger(RegisterControllerTest::class.java)
    }
}
