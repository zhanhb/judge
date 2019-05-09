package cn.edu.zjnu.acm.judge.controller.user

import cn.edu.zjnu.acm.judge.Application
import cn.edu.zjnu.acm.judge.service.MockDataService
import cn.edu.zjnu.acm.judge.util.JudgeUtils
import org.junit.jupiter.api.Test
import org.junit.platform.runner.JUnitPlatform
import org.junit.runner.RunWith
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
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
class UserListControllerTest {

    @Autowired
    private val mvc: MockMvc? = null
    @Autowired
    private val mockDataService: MockDataService? = null

    /**
     * Test of userList method, of class UserListController.
     *
     * [UserListController.userList]
     * [JudgeUtils.sequence]
     */
    @Test
    @Throws(Exception::class)
    fun testUserList() {
        log.info("userList")
        var result = mvc!!.perform(get("/userlist"))
                .andExpect(status().isOk)
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andReturn()
        for (i in 0..104) {
            mockDataService!!.user()
        }
        result = mvc.perform(get("/userlist"))
                .andExpect(status().isOk)
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andReturn()
        for (i in 0..999) {
            mockDataService!!.user()
        }
        for (page in intArrayOf(0, 1, 2, 7, 8, 9, 18, 19)) {
            result = mvc.perform(get("/userlist").param("page", Integer.toString(page)))
                    .andExpect(status().isOk)
                    .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                    .andReturn()
        }
    }

    companion object {
        private val log = LoggerFactory.getLogger(UserListControllerTest::class.java)
    }
}
