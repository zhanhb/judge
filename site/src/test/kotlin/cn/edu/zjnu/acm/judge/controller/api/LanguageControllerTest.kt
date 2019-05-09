package cn.edu.zjnu.acm.judge.controller.api

import cn.edu.zjnu.acm.judge.Application
import cn.edu.zjnu.acm.judge.domain.Language
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
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.transaction.annotation.Transactional

@AutoConfigureMockMvc
@RunWith(JUnitPlatform::class)
@SpringBootTest(classes = [Application::class])
@Transactional
@WebAppConfiguration
@WithMockUser(roles = ["ADMIN"])
class LanguageControllerTest {

    @Autowired
    private val mvc: MockMvc? = null
    @Autowired
    private val objectMapper: ObjectMapper? = null
    @Autowired
    private val mockDataService: MockDataService? = null

    /**
     * Test of findAll method, of class LanguageController.
     *
     * [LanguageController.findAll]
     */
    @Test
    @Throws(Exception::class)
    fun testFindAll() {
        log.info("findAll")
        val result = mvc!!.perform(get("/api/languages.json"))
                .andExpect(status().isOk)
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn()
    }

    /**
     * Test of save method, of class LanguageController.
     *
     * [LanguageController.save]
     */
    @Test
    @Throws(Exception::class)
    fun testSave() {
        log.info("save")
        val language = Language(
                name = "mock language",
                sourceExtension = "tmp",
                executableExtension = "dummy",
                description = "test description"
        )
        val result = mvc!!.perform(post("/api/languages.json")
                .content(objectMapper!!.writeValueAsString(language)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent)
                .andReturn()
    }

    private fun anyId(): Int {
        return mockDataService!!.anyLanguage().id
    }

    /**
     * Test of findOne method, of class LanguageController.
     *
     * [LanguageController.findOne]
     */
    @Test
    @Throws(Exception::class)
    fun testFindOne() {
        log.info("findOne")
        val id = anyId().toLong()
        val result = mvc!!.perform(get("/api/languages/{id}.json", id))
                .andExpect(status().isOk)
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn()
    }

    /**
     * Test of update method, of class LanguageController.
     *
     * [LanguageController.update]
     */
    @Test
    @Throws(Exception::class)
    fun testUpdate() {
        log.info("update")
        val id = anyId().toLong()
        val request = Language(
                name = "mock language",
                sourceExtension = "tmp",
                executableExtension = "dummy"
        )
        val result = mvc!!.perform(put("/api/languages/{id}.json", id)
                .content(objectMapper!!.writeValueAsString(request)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent)
                .andReturn()
    }

    /**
     * Test of delete method, of class LanguageController.
     *
     * [LanguageController.delete]
     */
    @Test
    @Throws(Exception::class)
    fun testDelete() {
        log.info("delete")
        val id = anyId().toLong()
        val result = mvc!!.perform(delete("/api/languages/{id}.json", id))
                .andExpect(status().isNoContent)
                .andReturn()
    }

    companion object {
        private val log = LoggerFactory.getLogger(LanguageControllerTest::class.java)
    }
}
