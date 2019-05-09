package cn.edu.zjnu.acm.judge.controller.problem

import cn.edu.zjnu.acm.judge.Application
import cn.edu.zjnu.acm.judge.domain.Problem
import cn.edu.zjnu.acm.judge.service.ProblemService
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

@AutoConfigureMockMvc
@RunWith(JUnitPlatform::class)
@SpringBootTest(classes = [Application::class])
@Transactional
@WebAppConfiguration
class ProblemStatusControllerTest {

    @Autowired
    private val mvc: MockMvc? = null
    @Autowired
    private val problemService: ProblemService? = null

    /**
     * Test of gotoProblem method, of class ProblemStatusController.
     *
     * [ProblemStatusController.gotoProblem]
     */
    @Test
    @Throws(Exception::class)
    fun testGotoProblem() {
        log.info("gotoProblem")
        var pid = "test"
        mvc!!.perform(get("/gotoproblem").param("pid", pid))
                .andExpect(redirectedUrl("/searchproblem?sstr=$pid"))
                .andReturn()
        pid = "1000"
        mvc.perform(get("/gotoproblem").param("pid", pid))
                .andExpect(redirectedUrl("/showproblem?problem_id=$pid"))
                .andReturn()
    }

    /**
     * Test of status method, of class ProblemStatusController.
     *
     * [ProblemStatusController.status]
     */
    @Test
    @Throws(Exception::class)
    fun testStatus() {
        log.info("status")
        val problem = Problem(contests = longArrayOf(0), timeLimit = 1000L, memoryLimit = 65536L)
        problemService!!.save(problem)
        val problemId = problem.id!!
        val result = mvc!!.perform(get("/problemstatus").param("problem_id", java.lang.Long.toString(problemId!!)))
                .andExpect(status().isOk)
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andReturn()
    }

    companion object {
        private val log = LoggerFactory.getLogger(ProblemStatusControllerTest::class.java)
    }
}
