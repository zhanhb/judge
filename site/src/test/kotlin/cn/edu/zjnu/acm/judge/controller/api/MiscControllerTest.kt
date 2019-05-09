package cn.edu.zjnu.acm.judge.controller.api

import cn.edu.zjnu.acm.judge.Application
import cn.edu.zjnu.acm.judge.data.dto.ValueHolder
import cn.edu.zjnu.acm.judge.data.form.SystemInfoForm
import com.fasterxml.jackson.databind.ObjectMapper
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import org.junit.platform.runner.JUnitPlatform
import org.junit.runner.RunWith
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.context.web.WebAppConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.transaction.annotation.Transactional

@AutoConfigureMockMvc
@RunWith(JUnitPlatform::class)
@SpringBootTest(classes = [Application::class])
@Transactional
@WebAppConfiguration
@WithMockUser(roles = ["ADMIN"])
class MiscControllerTest {

    @Autowired
    private val mvc: MockMvc? = null
    @Autowired
    private val objectMapper: ObjectMapper? = null

    private var contestOnly: Long?
        @Throws(Exception::class)
        get() {
            val type = objectMapper!!.typeFactory.constructParametricType(ValueHolder::class.java, Long::class.java)
            val (value) = objectMapper.readValue<ValueHolder<Long>>(mvc!!.perform(get("/api/misc/contestOnly.json"))
                    .andExpect(status().isOk)
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                    .andReturn().response.contentAsString, type)
            return value
        }
        @Throws(Exception::class)
        set(value) {
            val request = ValueHolder(value)
            val result = mvc!!.perform(put("/api/misc/contestOnly.json")
                    .content(objectMapper!!.writeValueAsString(request)).contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNoContent)
                    .andReturn()
        }

    /**
     * Test of fix method, of class MiscController.
     *
     * [MiscController.fix]
     */
    @Test
    @Throws(Exception::class)
    fun testFix() {
        log.info("fix")
        val result = mvc!!.perform(post("/api/misc/fix.json"))
                .andExpect(status().isNoContent)
                .andReturn()
    }

    /**
     * Test of setSystemInfo method, of class MiscController.
     *
     * [MiscController.setSystemInfo]
     */
    @Test
    @Throws(Exception::class)
    fun testSetSystemInfo() {
        log.info("setSystemInfo")
        val request = SystemInfoForm("test", false)
        val result = mvc!!.perform(put("/api/misc/systemInfo.json")
                .content(objectMapper!!.writeValueAsString(request)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent)
                .andReturn()
    }

    /**
     * Test of systemInfo method, of class MiscController.
     *
     * [MiscController.systemInfo]
     */
    @Test
    @Throws(Exception::class)
    fun testSystemInfo() {
        log.info("systemInfo")
        val result = mvc!!.perform(get("/api/misc/systemInfo.json"))
                .andExpect(status().isOk)
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn()
    }

    /**
     * Test of contestOnly method, of class MiscController.
     *
     * [MiscController.contestOnly]
     * [MiscController.setContestOnly]
     */
    @Test
    @Throws(Exception::class)
    fun testContestOnly() {
        log.info("getContestOnly")
        val old = contestOnly
        try {
            contestOnly = null
            assertNull(contestOnly)
            request(HttpStatus.OK)

            contestOnly = java.lang.Long.MIN_VALUE
            assertThat(contestOnly!!).isEqualTo(Long.MIN_VALUE)
            request(HttpStatus.BAD_REQUEST)

            contestOnly = java.lang.Long.MIN_VALUE
            request(HttpStatus.BAD_REQUEST)
            assertThat(contestOnly!!).isEqualTo(Long.MIN_VALUE)

            contestOnly = null
            request(HttpStatus.OK)
            assertNull(contestOnly)
        } finally {
            contestOnly = old
        }
    }

    @Throws(Exception::class)
    private fun request(status: HttpStatus) {
        mvc!!.perform(get("/registerpage"))
                .andExpect(status().`is`(status.value()))
        mvc.perform(get("/register"))
                .andExpect(status().`is`(status.value()))
    }

    companion object {
        private val log = LoggerFactory.getLogger(MiscControllerTest::class.java)
    }
}
