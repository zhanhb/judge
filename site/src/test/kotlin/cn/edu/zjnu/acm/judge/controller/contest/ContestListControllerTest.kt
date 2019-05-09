package cn.edu.zjnu.acm.judge.controller.contest

import cn.edu.zjnu.acm.judge.Application
import cn.edu.zjnu.acm.judge.data.form.ContestStatus
import cn.edu.zjnu.acm.judge.domain.Contest
import cn.edu.zjnu.acm.judge.service.ContestService
import org.hamcrest.Matchers.isIn
import org.hamcrest.Matchers.notNullValue
import org.junit.jupiter.api.Test
import org.junit.platform.runner.JUnitPlatform
import org.junit.runner.RunWith
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.http.MediaType.*
import org.springframework.test.context.web.WebAppConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.ResultMatcher
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.time.temporal.ChronoUnit

@AutoConfigureMockMvc
@RunWith(JUnitPlatform::class)
@SpringBootTest(classes = [Application::class])
@Transactional
@WebAppConfiguration
class ContestListControllerTest {

    @Autowired
    private val mvc: MockMvc? = null
    @Autowired
    private val contestService: ContestService? = null

    private val isOkOrFound: ResultMatcher
        get() = status().`is`(isIn(arrayOf(200, 302)))

    @Throws(Exception::class)
    private fun clearPending() {
        for ((id) in contestService!!.findAll(ContestStatus.PENDING)) {
            contestService.delete(id!!)
        }
    }

    @Throws(Exception::class)
    private fun contestTestBase(matcher: ResultMatcher, expect: MediaType?, vararg accept: MediaType): ResultActions {
        var builder = get("/scheduledcontests")
        if (accept.isNotEmpty()) {
            builder = builder.accept(*accept)
        }
        val resultActions = mvc!!.perform(builder).andExpect(matcher)
        if (expect !== null) {
            resultActions.andExpect(content().contentTypeCompatibleWith(expect))
        }
        return resultActions
    }

    @Test
    @Throws(Exception::class)
    fun testContentType() {
        clearPending()
        // first is matched
        contestTestBase(status().isOk, TEXT_HTML, TEXT_HTML)
        contestTestBase(status().isOk, TEXT_HTML, TEXT_HTML, APPLICATION_JSON)
        // the matching controller produces TEXT_HTML, but then we goto the error page, it supports produces APPLICATION_JSON,
        // so the content type is set by the controller advice class
        val result = contestTestBase(status().isOk, APPLICATION_JSON, APPLICATION_JSON, TEXT_HTML)
        result.andExpect(handler().handlerType(ContestListController::class.java))
                .andExpect(jsonPath("message", notNullValue()))
        // non matched
        contestTestBase(status().isNotAcceptable, null, IMAGE_JPEG, APPLICATION_JSON)
        contestTestBase(status().isNotAcceptable, null, IMAGE_JPEG)
        contestTestBase(status().isOk, TEXT_HTML)
        // match all
        contestTestBase(status().isOk, TEXT_HTML, ALL)
    }

    @Test
    @Throws(Exception::class)
    fun testRedirect() {
        clearPending()
        val start = Instant.now().plus(1, ChronoUnit.HOURS)
        val end = Instant.now().plus(4, ChronoUnit.HOURS)

        contestService!!.save(Contest(
                createdTime = Instant.now(),
                disabled = false,
                startTime = start,
                endTime = end,
                title = "sample contest",
                description = "no description"
        ))
        mvc!!.perform(get("/scheduledcontests"))
                .andExpect(status().isFound)
                .andReturn()
        contestService.save(Contest(createdTime = Instant.now(), disabled = false,
                startTime = start,
                endTime = end,
                title = "sample contest2",
                description = "no description2"
        ))
        mvc.perform(get("/scheduledcontests"))
                .andExpect(status().isOk)
                .andExpect(view().name("contests/index"))
                .andReturn()
    }

    /**
     * Test of contests method, of class ContestListController.
     *
     * [ContestListController.contests]
     */
    @Test
    @Throws(Exception::class)
    fun testContests() {
        log.info("contests")
        mvc!!.perform(get("/contests"))
                .andExpect(isOkOrFound)
                .andReturn()
    }

    /**
     * Test of scheduledContests method, of class ContestListController.
     *
     * [ContestListController.scheduledContests]
     */
    @Test
    @Throws(Exception::class)
    fun testScheduledContests() {
        log.info("scheduledContests")
        mvc!!.perform(get("/scheduledcontests"))
                .andExpect(isOkOrFound)
                .andReturn()
    }

    /**
     * Test of pastContests method, of class ContestListController.
     *
     * [ContestListController.pastContests]
     */
    @Test
    @Throws(Exception::class)
    fun testPastContests() {
        log.info("pastContests")
        mvc!!.perform(get("/pastcontests"))
                .andExpect(isOkOrFound)
                .andReturn()
    }

    /**
     * Test of currentContests method, of class ContestListController.
     *
     * [ContestListController.currentContests]
     */
    @Test
    @Throws(Exception::class)
    fun testCurrentContests() {
        log.info("currentContests")
        mvc!!.perform(get("/currentcontests"))
                .andExpect(isOkOrFound)
                .andReturn()
    }

    companion object {
        private val log = LoggerFactory.getLogger(ContestListControllerTest::class.java)
    }
}
