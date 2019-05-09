package cn.edu.zjnu.acm.judge.controller.legacy

import cn.edu.zjnu.acm.judge.Application
import org.junit.jupiter.api.Test
import org.junit.platform.runner.JUnitPlatform
import org.junit.runner.RunWith
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.context.web.WebAppConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl
import org.springframework.transaction.annotation.Transactional

@AutoConfigureMockMvc
@RunWith(JUnitPlatform::class)
@SpringBootTest(classes = [Application::class])
@Transactional
@WebAppConfiguration
@WithMockUser(roles = ["ADMIN"])
class LegacyAdminControllerTest {

    @Autowired
    private val mvc: MockMvc? = null

    /**
     * Test of showProblem method, of class LegacyAdminController.
     *
     * [LegacyAdminController.showProblem]
     */
    @Test
    @Throws(Exception::class)
    fun testShowProblem() {
        log.info("showProblem")
        val problemId: Long = 0
        val result = mvc!!.perform(get("/admin.showproblem").param("problem_id", java.lang.Long.toString(problemId)))
                .andExpect(redirectedUrl("/admin/problems/$problemId.html"))
                .andReturn()
    }

    /**
     * Test of showContest method, of class LegacyAdminController.
     *
     * [LegacyAdminController.showContest]
     */
    @Test
    @Throws(Exception::class)
    fun testShowContest() {
        log.info("showContest")
        val contestId: Long = 0
        val result = mvc!!.perform(get("/admin.showcontest").param("contest_id", java.lang.Long.toString(contestId)))
                .andExpect(redirectedUrl("/admin/contests/$contestId.html"))
                .andReturn()
    }

    /**
     * Test of rejudge method, of class LegacyAdminController.
     *
     * [LegacyAdminController.rejudge]
     */
    @Test
    @Throws(Exception::class)
    fun testRejudge() {
        log.info("rejudge")
        val result = mvc!!.perform(get("/admin.rejudge").param("contest_id", java.lang.Long.toString(1)))
                .andExpect(redirectedUrl("/admin/rejudge?contest_id=1"))
                .andReturn()
    }

    companion object {
        private val log = LoggerFactory.getLogger(LegacyAdminControllerTest::class.java)
    }
}
