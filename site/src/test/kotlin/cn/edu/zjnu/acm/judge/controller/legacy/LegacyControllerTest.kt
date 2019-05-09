package cn.edu.zjnu.acm.judge.controller.legacy

import cn.edu.zjnu.acm.judge.Application
import org.junit.jupiter.api.Test
import org.junit.platform.runner.JUnitPlatform
import org.junit.runner.RunWith
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
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
class LegacyControllerTest {

    @Autowired
    private val mvc: MockMvc? = null

    /**
     * Test of contestStanding method, of class LegacyController.
     *
     * [LegacyController.contestStanding]
     */
    @Test
    @Throws(Exception::class)
    fun testContestStanding() {
        log.info("contestStanding")
        val contestId: Long = 0
        val result = mvc!!.perform(get("/conteststanding").param("contest_id", java.lang.Long.toString(contestId)))
                .andExpect(redirectedUrl("/contests/$contestId/standing.html"))
                .andReturn()
    }

    /**
     * Test of showContest method, of class LegacyController.
     *
     * [LegacyController.showContest]
     */
    @Test
    @Throws(Exception::class)
    fun testShowContest() {
        log.info("showContest")
        val contestId: Long = 1058
        val result = mvc!!.perform(get("/showcontest").param("contest_id", java.lang.Long.toString(contestId)))
                .andExpect(redirectedUrl("/contests/$contestId/problems.html"))
                .andReturn()
    }

    companion object {
        private val log = LoggerFactory.getLogger(LegacyControllerTest::class.java)
    }
}
