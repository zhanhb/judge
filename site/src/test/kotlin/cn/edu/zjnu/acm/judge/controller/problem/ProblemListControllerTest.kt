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
package cn.edu.zjnu.acm.judge.controller.problem

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
import java.util.*

@AutoConfigureMockMvc
@RunWith(JUnitPlatform::class)
@SpringBootTest(classes = [Application::class])
@Transactional
@WebAppConfiguration
class ProblemListControllerTest {

    @Autowired
    private val mvc: MockMvc? = null

    /**
     * Test of problemList method, of class ProblemListController.
     *
     * [ProblemListController.problemList]
     */
    @Test
    @Throws(Exception::class)
    fun testProblemList() {
        log.info("problemList")
        val sstr = ""
        val disabled: Boolean? = null
        val locale = Locale.getDefault()
        val result = mvc!!.perform(get("/problemlist")
                .param("sstr", sstr)
                .param("disabled", disabled?.toString() ?: "")
                .locale(locale))
                .andExpect(status().isOk)
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andReturn()
    }

    /**
     * Test of searchProblem method, of class ProblemListController.
     *
     * [ProblemListController.searchProblem]
     */
    @Test
    @Throws(Exception::class)
    fun testSearchProblem() {
        log.info("searchProblem")
        val sstr = "test"
        val disabled: Boolean? = null
        val locale = Locale.getDefault()
        val result = mvc!!.perform(get("/searchproblem")
                .param("sstr", sstr)
                .param("disabled", disabled?.toString() ?: "")
                .locale(locale))
                .andExpect(status().isOk)
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andReturn()
    }

    companion object {
        private val log = LoggerFactory.getLogger(ProblemListControllerTest::class.java)
    }
}
