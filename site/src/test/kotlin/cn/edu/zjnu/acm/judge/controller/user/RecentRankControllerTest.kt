package cn.edu.zjnu.acm.judge.controller.user

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
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.transaction.annotation.Transactional

@AutoConfigureMockMvc
@RunWith(JUnitPlatform::class)
@SpringBootTest(classes = [Application::class])
@Transactional
@WebAppConfiguration
class RecentRankControllerTest {

    @Autowired
    private val mvc: MockMvc? = null

    /**
     * Test of recentRank method, of class RecentRankController.
     *
     * [RecentRankController.recentRank]
     */
    @Test
    @Throws(Exception::class)
    fun testRecentRank() {
        log.info("recentRank")
        val count = 200
        val result = mvc!!.perform(get("/recentrank").param("count", Integer.toString(count)))
                .andExpect(status().isOk)
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andReturn()
    }

    companion object {
        private val log = LoggerFactory.getLogger(RecentRankControllerTest::class.java)
    }
}
