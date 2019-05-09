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
package cn.edu.zjnu.acm.judge.config.security

import cn.edu.zjnu.acm.judge.Application
import cn.edu.zjnu.acm.judge.service.MockDataService
import org.junit.jupiter.api.Test
import org.junit.platform.runner.JUnitPlatform
import org.junit.runner.RunWith
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.web.WebAppConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.transaction.annotation.Transactional

/**
 *
 * @author zhanhb
 */
@AutoConfigureMockMvc
@RunWith(JUnitPlatform::class)
@SpringBootTest(classes = [Application::class])
@Transactional
@WebAppConfiguration
class SecurityConfigurationTest {

    @Autowired
    private val mvc: MockMvc? = null
    @Autowired
    private val mockDataService: MockDataService? = null

    /**
     * Test of login method, of class SecurityConfiguration.
     *
     * [SecurityConfiguration.configure]
     */
    @Test
    @Throws(Exception::class)
    fun testLogin() {
        log.info("login")
        val url = "/test"
        val user = mockDataService!!.user()
        val result = mvc!!.perform(post("/login")
                .param("user_id1", user.id!!)
                .param("password1", user.password!!)
                .param("url", url))
                .andExpect(status().isFound)
                .andExpect(redirectedUrl(url))
                .andReturn()
    }

    @Test
    @Throws(Exception::class)
    fun testLoginError() {
        log.info("login")
        val url = "/test?k=1&v=2"
        val redirectedUrl = "/login?error&url=/test?k%3D1%26v%3D2"
        val user = mockDataService!!.user()
        val result = mvc!!.perform(post("/login")
                .param("user_id1", user.id!!)
                .param("password1", user.password!! + 1)
                .param("url", url))
                .andExpect(status().isFound)
                .andExpect(redirectedUrl(redirectedUrl))
                .andReturn()
    }

    @Test
    @Throws(Exception::class)
    fun testLoginErrorWithoutRedirectParam() {
        log.info("login")
        val redirectedUrl = "/login?error"
        val user = mockDataService!!.user()
        mvc!!.perform(post("/login")
                .param("user_id1", user.id!!)
                .param("password1", user.password!! + 1))
                .andExpect(status().isFound)
                .andExpect(redirectedUrl(redirectedUrl))
                .andReturn()
        mvc.perform(post("/login")
                .param("user_id1", user.id!!)
                .param("password1", user.password!! + 1)
                .param("url", ""))
                .andExpect(status().isFound)
                .andExpect(redirectedUrl(redirectedUrl))
                .andReturn()
    }

    companion object {
        private val log = LoggerFactory.getLogger(SecurityConfigurationTest::class.java)
    }
}
