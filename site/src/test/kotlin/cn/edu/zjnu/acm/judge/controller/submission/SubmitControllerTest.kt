package cn.edu.zjnu.acm.judge.controller.submission

import cn.edu.zjnu.acm.judge.Application
import cn.edu.zjnu.acm.judge.service.MockDataService
import com.google.common.base.Strings
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
import java.util.*

@AutoConfigureMockMvc
@RunWith(JUnitPlatform::class)
@SpringBootTest(classes = [Application::class])
@Transactional
@WebAppConfiguration
class SubmitControllerTest {

    @Autowired
    private val mvc: MockMvc? = null
    @Autowired
    private val mockDataService: MockDataService? = null

    /**
     * Test of submitPage method, of class SubmitPageController.
     *
     * [SubmitController.submitPage]
     */
    @Test
    @Throws(Exception::class)
    fun testSubmitPage() {
        log.info("submitPage")
        val problem_id: Long? = null
        val contest_id: Long? = null
        val userId = mockDataService!!.user().id
        val result = mvc!!.perform(get("/submitpage").with(user(userId))
                .param("problem_id", problem_id?.toString() ?: "")
                .param("contest_id", contest_id?.toString() ?: ""))
                .andExpect(status().isOk)
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andReturn()
    }

    /**
     * Test of submit method, of class SubmitController.
     *
     * [SubmitController.submit]
     */
    @Test
    @Throws(Exception::class)
    fun testSubmit() {
        log.info("submit")
        val language = mockDataService!!.anyLanguage().id
        val problemId = mockDataService.problem().id
        val source = Strings.repeat(" ", 20)
        val userId = mockDataService.user().id
        val result = mvc!!.perform(post("/problems/{problemId}/submit", problemId).with(user(userId))
                .param("language", Integer.toString(language))
                .param("source", source))
                .andExpect(status().isFound)
                .andExpect(redirectedUrl("/status?user_id=$userId"))
                .andReturn()
    }

    companion object {
        private val log = LoggerFactory.getLogger(SubmitControllerTest::class.java)
    }
}
