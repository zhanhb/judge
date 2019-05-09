/*
 * Copyright 2017 ZJNU ACM.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.edu.zjnu.acm.judge.controller

import cn.edu.zjnu.acm.judge.Application
import cn.edu.zjnu.acm.judge.mapper.MessageMapper
import cn.edu.zjnu.acm.judge.service.MessageService
import cn.edu.zjnu.acm.judge.service.MockDataService
import org.junit.jupiter.api.Test
import org.junit.platform.runner.JUnitPlatform
import org.junit.runner.RunWith
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.anonymous
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user
import org.springframework.test.context.web.WebAppConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.request.RequestPostProcessor
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.transaction.annotation.Transactional
import java.util.*

/**
 *
 * @author zhanhb
 */
@AutoConfigureMockMvc
@RunWith(JUnitPlatform::class)
@SpringBootTest(classes = [Application::class])
@Transactional
@WebAppConfiguration
class BBSControllerTest {

    @Autowired
    private val mvc: MockMvc? = null
    @Autowired
    private val messageService: MessageService? = null
    @Suppress("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private val messageMapper: MessageMapper? = null
    @Autowired
    private val mockDataService: MockDataService? = null

    /**
     * Test of bbs method, of class BBSController.
     *
     * [BBSController.bbs]
     */
    @Test
    @Throws(Exception::class)
    fun testBbs() {
        log.info("bbs")
        val problemId: Long? = null
        val size = 50
        val top: Long = 99999999
        val result = mvc!!.perform(get("/bbs")
                .param("problem_id", problemId?.toString() ?: "")
                .param("size", Integer.toString(size))
                .param("top", java.lang.Long.toString(top)))
                .andExpect(status().isOk)
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andReturn()
    }

    @Throws(Exception::class)
    private fun testPostpage0(postProcessor: RequestPostProcessor): ResultActions {
        log.info("postpage")
        val problemId: Long? = null
        return mvc!!.perform(get("/postpage").with(postProcessor)
                .param("problem_id", problemId?.toString() ?: ""))
    }

    /**
     * Test of postpage method, of class BBSController.
     *
     * [BBSController.postpage]
     */
    @Test
    @Throws(Exception::class)
    fun testPostpage() {
        val userId = createUser()
        testPostpage0(user(userId))
                .andExpect(status().isOk)
                .andExpect(view().name("bbs/postpage"))
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andReturn()
    }

    /**
     * Test of postpage method, of class BBSController.
     *
     * [BBSController.postpage]
     */
    @Test
    @Throws(Exception::class)
    fun testPostpageAnonymous() {
        testPostpage0(anonymous())
                .andExpect(forwardedUrl("/unauthorized"))
                .andReturn()
    }

    /**
     * Test of post method, of class BBSController.
     *
     * [BBSController.post]
     */
    @Test
    @Throws(Exception::class)
    fun testPost() {
        log.info("post")
        val problemIds = arrayOf(null, mockDataService!!.problem().id)
        val parentId: Long? = null
        val content = "test"
        val title = "title"
        val user = user(createUser())
        for (problemId in problemIds) {
            val redirectedUrl = "/bbs" + if (problemId != null) "?problem_id=$problemId" else ""
            val result = mvc!!.perform(post("/post").with(user)
                    .param("problem_id", problemId?.toString() ?: "")
                    .param("parent_id", parentId?.toString() ?: "")
                    .param("content", content)
                    .param("title", title))
                    .andExpect(status().isFound)
                    .andExpect(redirectedUrl(redirectedUrl))
                    .andReturn()
        }
        mvc!!.perform(get("/bbs").with(user))
                .andExpect(status().is2xxSuccessful)
    }

    /**
     * Test of showMessage method, of class BBSController.
     *
     * [BBSController.showMessage]
     */
    @Test
    @Throws(Exception::class)
    fun testShowMessage() {
        log.info("showMessage")
        val userId = createUser()
        val messageId = messageMapper!!.nextId()
        messageService!!.save(null, null, userId, "title", "content")
        mvc!!.perform(get("/showmessage").with(user(userId))
                .param("message_id", java.lang.Long.toString(messageId)))
                .andExpect(status().isOk)
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andReturn()
    }

    private fun createUser(): String {
        return mockDataService!!.user().id!!
    }

    companion object {
        private val log = LoggerFactory.getLogger(BBSControllerTest::class.java)
    }
}
