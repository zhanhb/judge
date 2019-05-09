package cn.edu.zjnu.acm.judge.controller.api

import cn.edu.zjnu.acm.judge.Application
import cn.edu.zjnu.acm.judge.domain.Contest
import cn.edu.zjnu.acm.judge.service.MockDataService
import com.fasterxml.jackson.databind.ObjectMapper
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.transaction.annotation.Transactional
import java.util.*

@AutoConfigureMockMvc(addFilters = false)
@RunWith(JUnitPlatform::class)
@SpringBootTest(classes = [Application::class])
@Transactional
@WebAppConfiguration
@WithMockUser(roles = ["ADMIN"])
class ContestControllerTest {

    @Autowired
    private val mvc: MockMvc? = null
    @Autowired
    private val objectMapper: ObjectMapper? = null
    @Autowired
    private val mockDataService: MockDataService? = null

    /**
     * Test of save method, of class ContestController.
     *
     * [ContestController.save]
     */
    @Test
    @Throws(Exception::class)
    fun testSave() {
        log.info("save")
        val contest = mockDataService!!.contest(false)
        val result = mvc!!.perform(post("/api/contests.json")
                .content(objectMapper!!.writeValueAsString(contest)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk)
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn()
    }

    /**
     * Test of delete method, of class ContestController.
     *
     * [ContestController.delete]
     */
    @Test
    @Throws(Exception::class)
    fun testDelete() {
        log.info("delete")
        val id = mockDataService!!.contest().id!!
        val result = mvc!!.perform(delete("/api/contests/{id}.json", id))
                .andExpect(status().isNoContent)
                .andReturn()
    }

    /**
     * Test of list method, of class ContestController.
     *
     * [ContestController.list]
     */
    @Test
    @Throws(Exception::class)
    fun testList() {
        log.info("list")
        val includeDisabled = false
        val exclude: Array<String>? = null
        val include: Array<String>? = null
        val result = mvc!!.perform(get("/api/contests.json")
                .param("includeDisabled", java.lang.Boolean.toString(includeDisabled))
                .param("exclude", exclude?.toString() ?: "")
                .param("include", include?.toString() ?: ""))
                .andExpect(status().isOk)
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn()
    }

    /**
     * Test of findOne method, of class ContestController.
     *
     * [ContestController.findOne]
     */
    @Test
    @Throws(Exception::class)
    fun testFindOne() {
        log.info("findOne")
        val id = mockDataService!!.contest().id!!
        val locale = Locale.getDefault()
        val result = mvc!!.perform(get("/api/contests/{id}.json", id)
                .locale(locale))
                .andExpect(status().isOk)
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn()
    }

    /**
     * Test of update method, of class ContestController.
     *
     * [ContestController.update]
     */
    @Test
    @Throws(Exception::class)
    fun testUpdate() {
        log.info("update")
        val id = mockDataService!!.contest().id!!
        val request = Contest()
        val result = mvc!!.perform(patch("/api/contests/{id}.json", id)
                .content(objectMapper!!.writeValueAsString(request)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent)
                .andReturn()
    }

    /**
     * Test of standing method, of class ContestController.
     *
     * [ContestController.standing]
     */
    @Test
    @Throws(Exception::class)
    fun testStanding() {
        log.info("standing")
        val id = mockDataService!!.contest().id!!
        val result = mvc!!.perform(get("/api/contests/{id}/standing.json", id))
                .andExpect(request().asyncStarted())
                .andReturn()
        val asyncResult = mvc.perform(asyncDispatch(result))
                .andExpect(status().isOk)
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn()
    }

    companion object {
        private val log = LoggerFactory.getLogger(ContestControllerTest::class.java)
    }
}
