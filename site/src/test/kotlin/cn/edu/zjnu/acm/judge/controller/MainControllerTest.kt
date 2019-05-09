package cn.edu.zjnu.acm.judge.controller

import cn.edu.zjnu.acm.judge.Application
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
class MainControllerTest {

    @Autowired
    private val mvc: MockMvc? = null

    /**
     * Test of index method, of class MainController.
     *
     * [MainController.index]
     */
    @Test
    @Throws(Exception::class)
    fun testIndex() {
        log.info("index")
        val result = mvc!!.perform(get("/"))
                .andExpect(status().isOk)
                .andExpect(view().name("index"))
                .andReturn()
    }

    /**
     * Test of faq method, of class MainController.
     *
     * [MainController.faq]
     */
    @Test
    @Throws(Exception::class)
    fun testFaq() {
        log.info("faq")
        val result = mvc!!.perform(get("/faq"))
                .andExpect(status().isOk)
                .andExpect(view().name("faq"))
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andReturn()
    }

    /**
     * Test of findPassword method, of class MainController.
     *
     * [MainController.findPassword]
     */
    @Test
    @Throws(Exception::class)
    fun testFindPassword() {
        log.info("findPassword")
        val result = mvc!!.perform(get("/findpassword"))
                .andExpect(status().isOk)
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andReturn()
    }

    /**
     * Test of registerPage method, of class MainController.
     *
     * [MainController.registerPage]
     */
    @Test
    @Throws(Exception::class)
    fun testRegisterPage() {
        log.info("registerPage")
        val result = mvc!!.perform(get("/registerpage"))
                .andExpect(status().isOk)
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andReturn()
    }

    companion object {
        private val log = LoggerFactory.getLogger(MainControllerTest::class.java)
    }
}
