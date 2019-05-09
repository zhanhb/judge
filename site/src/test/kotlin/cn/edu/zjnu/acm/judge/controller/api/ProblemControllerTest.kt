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
import cn.edu.zjnu.acm.judge.domain.Problem
import cn.edu.zjnu.acm.judge.exception.BusinessCode
import cn.edu.zjnu.acm.judge.exception.BusinessException
import cn.edu.zjnu.acm.judge.service.MockDataService
import cn.edu.zjnu.acm.judge.service.ProblemService
import com.fasterxml.jackson.databind.ObjectMapper
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.*
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

/**
 *
 * @author zhanhb
 */
@AutoConfigureMockMvc
@RunWith(JUnitPlatform::class)
@SpringBootTest(classes = [Application::class])
@Transactional
@WebAppConfiguration
@WithMockUser(roles = ["ADMIN"])
class ProblemControllerTest {

    @Autowired
    private val objectMapper: ObjectMapper? = null
    @Autowired
    private val problemService: ProblemService? = null
    @Autowired
    private val mvc: MockMvc? = null
    @Suppress("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private val mockDataService: MockDataService? = null

    /**
     * Test of list method, of class ProblemController.
     *
     * [ProblemController.list]
     */
    @Test
    @Throws(Exception::class)
    fun testList() {
        mvc!!.perform(get("/api/problems.json"))
                .andExpect(status().isOk)
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
    }

    /**
     * [ProblemController.save]
     * [ProblemController.findOne]
     * [ProblemController.update]
     * [ProblemController.delete]
     */
    @Test
    @Throws(Exception::class)
    fun test() {
        val problem = mockDataService!!.problem(false)
        val id = objectMapper!!.readValue(mvc!!.perform(post("/api/problems.json")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(problem))
        )
                .andExpect(status().isOk)
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn().response.contentAsString, Problem::class.java).id
        assertThat(id).withFailMessage("problem id").isNotNull()

        assertFalse(findOne(id!!).disabled!!)
        mvc.perform(patch("/api/problems/{id}.json", id).content("{\"disabled\":true}").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent)
        assertTrue(findOne(id).disabled!!)

        mvc.perform(patch("/api/problems/{id}.json", id).content("{\"disabled\":false}").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent)
        assertFalse(findOne(id).disabled!!)

        mvc.perform(delete("/api/problems/{id}.json", id))
                .andExpect(status().isNoContent)

        val code = assertThrows(BusinessException::class.java) { problemService!!.findOne(id) }.code
        assertThat(code).isEqualTo(BusinessCode.PROBLEM_NOT_FOUND)

        mvc.perform(get("/api/problems/{id}.json", id))
                .andExpect(status().isNotFound)
        mvc.perform(delete("/api/problems/{id}.json", id))
                .andExpect(status().isNotFound)
        mvc.perform(patch("/api/problems/{id}.json", id)
                .content("{}").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound)
    }

    @Throws(Exception::class)
    private fun findOne(id: Long): Problem {
        return objectMapper!!.readValue(mvc!!.perform(get("/api/problems/{id}.json", id))
                .andExpect(status().isOk)
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn().response.contentAsString, Problem::class.java)
    }

    /**
     * Test of dataDir method, of class ProblemController.
     *
     * [ProblemController.dataDir]
     */
    @Test
    @Throws(Exception::class)
    fun testDataDir() {
        log.info("dataDir")
        val problem = mockDataService!!.problem()
        val result = mvc!!.perform(get("/api/problems/{id}/dataDir", problem.id!!).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk)
                .andReturn()
        objectMapper!!.readTree(result.response.contentAsString).hasNonNull("dataDir")
    }

    companion object {
        private val log = LoggerFactory.getLogger(ProblemControllerTest::class.java)
    }
}
