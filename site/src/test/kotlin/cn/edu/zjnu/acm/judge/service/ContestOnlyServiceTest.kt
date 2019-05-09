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
package cn.edu.zjnu.acm.judge.service

import cn.edu.zjnu.acm.judge.Application
import org.junit.jupiter.api.Test
import org.junit.platform.runner.JUnitPlatform
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.test.context.web.WebAppConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.transaction.annotation.Transactional

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertNull
import org.slf4j.LoggerFactory
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
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
class ContestOnlyServiceTest {

    @Autowired
    private val contestOnlyService: ContestOnlyService? = null
    @Autowired
    private val mvc: MockMvc? = null

    /**
     * Test of getContestOnly method, of class ContestOnlyService.
     */
    @Test
    @Throws(Exception::class)
    fun testGetContestOnly() {
        log.info("getContestOnly")
        val old = contestOnlyService!!.contestOnly
        try {
            contestOnlyService.contestOnly = null
            request(HttpStatus.OK)

            contestOnlyService.contestOnly = Long.MIN_VALUE
            assertThat(contestOnlyService.contestOnly!!).isEqualTo(Long.MIN_VALUE)
            request(HttpStatus.BAD_REQUEST)

            contestOnlyService.contestOnly = java.lang.Long.MIN_VALUE
            request(HttpStatus.BAD_REQUEST)
            assertThat(contestOnlyService.contestOnly!!).isEqualTo(Long.MIN_VALUE)

            contestOnlyService.contestOnly = null
            request(HttpStatus.OK)
            assertNull(contestOnlyService.contestOnly)
        } finally {
            contestOnlyService.contestOnly = old
        }
    }

    @Throws(Exception::class)
    private fun request(status: HttpStatus) {
        mvc!!.perform(get("/registerpage"))
                .andExpect(status().`is`(status.value()))
        mvc.perform(get("/register"))
                .andExpect(status().`is`(status.value()))
    }

    companion object {
        private val log = LoggerFactory.getLogger(ContestOnlyServiceTest::class.java)
    }
}
