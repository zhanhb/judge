package cn.edu.zjnu.acm.judge.controller.api

import cn.edu.zjnu.acm.judge.Application
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
@WithMockUser(roles = ["ADMIN"])
class LocaleControllerTest {

    @Autowired
    private val mvc: MockMvc? = null

    /**
     * Test of current method, of class LocaleController.
     *
     * [LocaleController.current]
     */
    @Test
    @Throws(Exception::class)
    fun testCurrent() {
        log.info("current")
        val locale = Locale.getDefault()
        val result = mvc!!.perform(get("/api/locales/current.json")
                .locale(locale))
                .andExpect(status().isOk)
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn()
    }

    /**
     * Test of findOne method, of class LocaleController.
     *
     * [LocaleController.findOne]
     */
    @Test
    @Throws(Exception::class)
    fun testFindOne() {
        log.info("findOne")
        val id = "en"
        val support = false
        val result = mvc!!.perform(get("/api/locales/{id}.json", id)
                .param("support", java.lang.Boolean.toString(support)))
                .andExpect(status().isOk)
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn()
    }

    /**
     * Test of findAll method, of class LocaleController.
     *
     * [LocaleController.findAll]
     */
    @Test
    @Throws(Exception::class)
    fun testFindAll() {
        log.info("findAll")
        val result = mvc!!.perform(get("/api/locales.json"))
                .andExpect(status().isOk)
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn()
    }

    /**
     * Test of supported method, of class LocaleController.
     *
     * [LocaleController.supported]
     */
    @Test
    @Throws(Exception::class)
    fun testSupported() {
        log.info("supported")
        val all = false
        val result = mvc!!.perform(get("/api/locales.json").param("all", java.lang.Boolean.toString(all)))
                .andExpect(status().isOk)
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn()
    }

    companion object {
        private val log = LoggerFactory.getLogger(LocaleControllerTest::class.java)
    }
}
