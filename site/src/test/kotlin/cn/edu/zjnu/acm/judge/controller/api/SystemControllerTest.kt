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
package cn.edu.zjnu.acm.judge.controller.api

import cn.edu.zjnu.acm.judge.Application
import cn.edu.zjnu.acm.judge.data.dto.ValueHolder
import com.fasterxml.jackson.databind.ObjectMapper
import org.assertj.core.api.Assertions.assertThat
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
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
class SystemControllerTest {

    @Autowired
    private val mvc: MockMvc? = null
    @Autowired
    private val objectMapper: ObjectMapper? = null

    private var index: String?
        @Throws(Exception::class)
        get() {
            val type = objectMapper!!.typeFactory.constructParametricType(ValueHolder::class.java, String::class.java)
            val (value) = objectMapper.readValue<ValueHolder<String>>(mvc!!.perform(get("/api/system/index")
                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk)
                    .andReturn().response.contentAsString, type)
            return value
        }
        @Throws(Exception::class)
        set(index) {
            val request = ValueHolder(index)
            mvc!!.perform(put("/api/system/index")
                    .content(objectMapper!!.writeValueAsString(request)).contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().is2xxSuccessful)
        }

    /**
     * Test of time method, of class SystemController.
     *
     * [SystemController.time]
     */
    @Test
    @Throws(Exception::class)
    fun testTime() {
        log.info("time")
        val result = mvc!!.perform(get("/api/system/time.json"))
                .andExpect(status().isOk)
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn()
    }

    /**
     * Test of index method, of class SystemController. Test of setIndex method,
     * of class SystemController.
     *
     * [SystemController.index]
     * [SystemController.setIndex]
     */
    @Test
    @WithMockUser(roles = ["ADMIN"])
    @Throws(Exception::class)
    fun testIndex() {
        log.info("index")
        val old = index
        try {
            val tmp = "test result"
            index = tmp
            assertThat(index).isEqualTo(tmp)
        } finally {
            index = old
        }
    }

    companion object {
        private val log = LoggerFactory.getLogger(SystemControllerTest::class.java)
    }
}
