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
class ModifyUserControllerTest {

    @Autowired
    private val mvc: MockMvc? = null
    @Autowired
    private val mockDataService: MockDataService? = null

    /**
     * Test of updatePage method, of class ModifyUserController.
     *
     * [ModifyUserController.updatePage]
     */
    @Test
    @Throws(Exception::class)
    fun testUpdatePage() {
        log.info("updatePage")
        val userId = mockDataService!!.user().id
        val result = mvc!!.perform(get("/modifyuserpage").with(user(userId)))
                .andExpect(status().isOk)
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andReturn()
    }

    /**
     * Test of update method, of class ModifyUserController.
     *
     * [ModifyUserController.update]
     */
    @Test
    @Throws(Exception::class)
    fun testUpdate() {
        log.info("update")
        val user = mockDataService!!.user()
        val userId = user.id
        val oldPassword = user.password
        val newPassword = "" // at least 6 characters or empty
        val email = ""
        val nick = ""
        val school = ""
        val result = mvc!!.perform(post("/modifyuser").with(user(userId!!))
                .param("oldPassword", oldPassword!!)
                .param("newPassword", newPassword)
                .param("rptPassword", newPassword)
                .param("email", email)
                .param("nick", nick)
                .param("school", school))
                .andExpect(status().isOk)
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(view().name("users/modifySuccess"))
                .andReturn()
    }

    companion object {
        private val log = LoggerFactory.getLogger(ModifyUserControllerTest::class.java)
    }
}
