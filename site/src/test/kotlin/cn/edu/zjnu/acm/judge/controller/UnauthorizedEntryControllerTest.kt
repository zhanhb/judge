/*
 * Copyright 2019 ZJNU ACM.
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
import org.junit.jupiter.api.Test
import javax.servlet.http.HttpServletRequest
import org.junit.platform.runner.JUnitPlatform
import org.junit.runner.RunWith
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.web.WebAppConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.MvcResult
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.servlet.mvc.support.RedirectAttributes

import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

/**
 *
 * @author zhanhb
 */
@AutoConfigureMockMvc
@RunWith(JUnitPlatform::class)
@SpringBootTest(classes = [Application::class])
@Transactional
@WebAppConfiguration
class UnauthorizedEntryControllerTest {

    @Autowired
    private val mvc: MockMvc? = null

    /**
     * Test of unauthorizedHtml method, of class UnauthorizedEntryController.
     *
     * [UnauthorizedEntryController.unauthorizedHtml]
     */
    @Test
    @Throws(Exception::class)
    fun testUnauthorizedHtml() {
        log.info("unauthorizedHtml")
        val result = mvc!!.perform(get("/unauthorized").accept(MediaType.TEXT_HTML))
                .andExpect(status().isFound)
                .andExpect(redirectedUrl("/login"))
                .andReturn()
    }

    /**
     * Test of unauthorized method, of class UnauthorizedEntryController.
     *
     * [UnauthorizedEntryController.unauthorized]
     */
    @Test
    @Throws(Exception::class)
    fun testUnauthorized() {
        log.info("unauthorized")
        val result = mvc!!.perform(get("/unauthorized").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized)
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn()
    }

    companion object {
        private val log = LoggerFactory.getLogger(UnauthorizedEntryControllerTest::class.java)
    }
}
