/*
 * Copyright 2016 ZJNU ACM.
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
package cn.edu.zjnu.acm.judge.controller.problem

import cn.edu.zjnu.acm.judge.Application
import cn.edu.zjnu.acm.judge.service.MockDataService
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
class ShowProblemControllerTest {

    @Autowired
    private val mvc: MockMvc? = null
    @Autowired
    private val mockDataService: MockDataService? = null

    /**
     * Test of showProblem method, of class ShowProblemController.
     *
     * [ShowProblemController.showProblem]
     */
    @Test
    @Throws(Exception::class)
    fun testShowProblem() {
        log.info("showProblem")
        val problemId = mockDataService!!.problem().id!!
        val locale = Locale.getDefault()
        val result = mvc!!.perform(get("/showproblem").param("problem_id", problemId.toString())
                .locale(locale))
                .andExpect(status().isOk)
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andReturn()
    }

    @Test
    @Throws(Exception::class)
    fun testShowproblemNotFound() {
        mvc!!.perform(get("/showproblem").param("problem_id", "999"))
                .andExpect(status().isNotFound)
    }

    companion object {
        private val log = LoggerFactory.getLogger(ShowProblemControllerTest::class.java)
    }
}
