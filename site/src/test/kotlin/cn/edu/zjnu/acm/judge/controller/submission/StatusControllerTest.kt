package cn.edu.zjnu.acm.judge.controller.submission

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
import org.springframework.test.context.web.WebAppConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.transaction.annotation.Transactional
import java.util.*

@AutoConfigureMockMvc
@RunWith(JUnitPlatform::class)
@SpringBootTest(classes = [Application::class])
@Transactional
@WebAppConfiguration
class StatusControllerTest {

    @Autowired
    private val mvc: MockMvc? = null
    @Autowired
    private val mockDataService: MockDataService? = null

    /**
     * Test of status method, of class StatusController.
     * [StatusController.status]
     */
    @Test
    @Throws(Exception::class)
    fun testStatus() {
        log.info("status")
        val problemId = ""
        val contestId: Long? = null
        val language = mockDataService!!.anyLanguage().id
        val size = 20
        val bottoms = arrayOf(null, 2000L)
        val score: Int? = null
        val userId = ""
        val top: Long? = null
        for (i in 0..99) {
            mockDataService.submission(false)
            for (bottom in bottoms) {
                val result = mvc!!.perform(get("/status")
                        .param("problem_id", problemId)
                        .param("contest_id", contestId?.toString() ?: "")
                        .param("language", Integer.toString(language))
                        .param("size", Integer.toString(size))
                        .param("bottom", bottom?.toString() ?: "")
                        .param("score", score?.toString() ?: "")
                        .param("user_id", userId)
                        .param("top", top?.toString() ?: ""))
                        .andExpect(status().isOk)
                        .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                        .andReturn()
            }
        }
    }

    companion object {
        private val log = LoggerFactory.getLogger(StatusControllerTest::class.java)
    }
}
