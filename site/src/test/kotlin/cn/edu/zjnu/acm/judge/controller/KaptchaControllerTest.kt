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
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.platform.runner.JUnitPlatform
import org.junit.runner.RunWith
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.web.WebAppConfiguration
import org.springframework.test.util.AssertionErrors.assertNotEquals
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content

/**
 *
 * @author zhanhb
 */
@AutoConfigureMockMvc
@RunWith(JUnitPlatform::class)
@SpringBootTest(classes = [Application::class])
@WebAppConfiguration
class KaptchaControllerTest {

    @Autowired
    private val mockMvc: MockMvc? = null

    /**
     * Test of service method, of class KaptchaController.
     *
     * [KaptchaController.service]
     */
    @Test
    @Throws(Exception::class)
    fun testService() {
        log.info("doGet")
        val result = mockMvc!!.perform(get("/images/rand.jpg"))
                .andExpect(content().contentTypeCompatibleWith(MediaType.IMAGE_JPEG))
                .andReturn()
        val session = result.request.getSession(false)
        assertThat(session).withFailMessage("no session").isNotNull()
        val response = result.response
        val body = response.contentAsByteArray
        assertThat(body).withFailMessage("body").isNotNull().isNotEmpty()
        assertNotEquals("empty body", 0, body.size)
        assertThat(session!!.getAttribute(com.google.code.kaptcha.Constants.KAPTCHA_SESSION_KEY)).isNotNull()
    }

    companion object {
        private val log = LoggerFactory.getLogger(KaptchaControllerTest::class.java)
    }
}
