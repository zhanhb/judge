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
package cn.edu.zjnu.acm.judge.mapper

import cn.edu.zjnu.acm.judge.Application
import cn.edu.zjnu.acm.judge.service.MockDataService
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.platform.runner.JUnitPlatform
import org.junit.runner.RunWith
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.web.WebAppConfiguration
import org.springframework.transaction.annotation.Transactional

/**
 *
 * @author zhanhb
 */
@RunWith(JUnitPlatform::class)
@SpringBootTest(classes = [Application::class])
@Transactional
@WebAppConfiguration
class UserMapperTest {

    @Autowired
    private val instance: UserMapper? = null
    @Autowired
    private val mockDataService: MockDataService? = null

    /**
     * Test of findOne method, of class UserMapper.
     */
    @Test
    fun testFindOne() {
        log.info("findOne")
        val id = mockDataService!!.user().id!!
        val result = instance!!.findOne(id)!!.id
        assertThat(result).isEqualTo(id)
    }

    @Test
    fun testNeighbours() {
        log.info("neighbours")
        val userId = mockDataService!!.user().id!!
        val result = instance!!.neighbours(userId, 4)
        log.info("{}", result)
    }

    companion object {
        private val log = LoggerFactory.getLogger(UserMapperTest::class.java)
    }
}
