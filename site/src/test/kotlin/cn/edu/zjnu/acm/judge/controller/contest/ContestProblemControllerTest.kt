package cn.edu.zjnu.acm.judge.controller.contest

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
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.transaction.annotation.Transactional
import java.util.*

@AutoConfigureMockMvc
@RunWith(JUnitPlatform::class)
@SpringBootTest(classes = [Application::class])
@Transactional
@WebAppConfiguration
class ContestProblemControllerTest {

    @Autowired
    private val mvc: MockMvc? = null
    @Autowired
    private val mockDataService: MockDataService? = null

    /**
     * Test of problems method, of class ContestProblemController.
     *
     * [ContestProblemController.problems]
     */
    @Test
    @Throws(Exception::class)
    fun testProblems() {
        log.info("problems")
        val contestId = mockDataService!!.contest().id!!
        val locale = Locale.getDefault()
        val result = mvc!!.perform(get("/contests/{contestId}/problems", contestId)
                .locale(locale))
                .andExpect(status().isOk)
                .andExpect(view().name("contests/problems"))
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andReturn()
    }

    /**
     * Test of showProblem method, of class ContestProblemController.
     *
     * [ContestProblemController.showProblem]
     */
    @Test
    @Throws(Exception::class)
    fun testShowProblem() {
        log.info("showProblem")
        val contestId = mockDataService!!.contest().id!!
        val pid = mockDataService.problem({ problem -> problem.copy(contests = longArrayOf(contestId)) }).id!!
        val locale = Locale.getDefault()
        mvc!!.perform(get("/contests/{contestId}/problems/{pid}", contestId, 1000)
                .locale(locale))
                .andExpect(status().isOk)
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(view().name("contests/problem"))
                .andReturn()
    }

    /**
     * Test of status method, of class ContestProblemController.
     *
     * [ContestProblemController.status]
     */
    @Test
    @Throws(Exception::class)
    fun testStatus() {
        log.info("status")
        val contestId = mockDataService!!.contest().id!!
        val pid = mockDataService.problem({ problem -> problem.copy(contests = longArrayOf(contestId)) }).id!!
        mvc!!.perform(get("/contests/{contestId}/problems/{pid}/status", contestId, 1000))
                .andExpect(status().isOk)
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andReturn()
    }

    companion object {
        private val log = LoggerFactory.getLogger(ContestProblemControllerTest::class.java)
    }
}
