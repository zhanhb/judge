package cn.edu.zjnu.acm.judge.controller

import cn.edu.zjnu.acm.judge.Application
import org.junit.jupiter.api.Test
import org.junit.platform.runner.JUnitPlatform
import org.junit.runner.RunWith
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.context.web.WebAppConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.transaction.annotation.Transactional

@AutoConfigureMockMvc
@RunWith(JUnitPlatform::class)
@SpringBootTest(classes = [Application::class])
@Transactional
@WebAppConfiguration
@WithMockUser(roles = ["ADMIN"])
class ManagerControllerTest {

    @Autowired
    private val mvc: MockMvc? = null

    /**
     * Test of manager method, of class ManageController.
     *
     * [ManagerController.manager]
     */
    @Test
    @Throws(Exception::class)
    fun testManager() {
        log.info("manager")
        val result = mvc!!.perform(get("/admin/"))
                .andExpect(status().isOk)
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andReturn()
    }

    companion object {
        private val log = LoggerFactory.getLogger(ManagerControllerTest::class.java)
    }
}
