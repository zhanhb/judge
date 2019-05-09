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
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user
import org.springframework.test.context.web.WebAppConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.transaction.annotation.Transactional

@AutoConfigureMockMvc
@RunWith(JUnitPlatform::class)
@SpringBootTest(classes = [Application::class])
@Transactional
@WebAppConfiguration
class ShowSubmissionDetailsControllerTest {

    @Autowired
    private val mvc: MockMvc? = null
    @Autowired
    private val mockDataService: MockDataService? = null

    /**
     * Test of showSolutionDetails method, of class
     * ShowSubmissionDetailsController.
     *
     * [ShowSubmissionDetailsController.showSolutionDetails]
     */
    @Test
    @Throws(Exception::class)
    fun testShowSolutionDetails() {
        log.info("showSolutionDetails")
        val submission = mockDataService!!.submission(false)
        val solutionId = submission.id
        val result = mvc!!.perform(get("/showsolutiondetails").with(user(submission.user!!))
                .param("solution_id", java.lang.Long.toString(solutionId)))
                .andExpect(status().isOk)
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(view().name("submissions/detail"))
                .andReturn()
    }

    companion object {
        private val log = LoggerFactory.getLogger(ShowSubmissionDetailsControllerTest::class.java)
    }
}
