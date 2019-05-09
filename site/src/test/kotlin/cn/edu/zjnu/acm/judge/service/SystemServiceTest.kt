/*
 * Copyright 2015 Pivotal Software, Inc..
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
class SystemServiceTest {

    @Autowired
    private val systemService: SystemService? = null

    /**
     * Test of getDataDirectory method, of class SystemService.
     */
    @Test
    fun testGetDataDirectory() {
        log.info("getDataDirectory")
        val problemId = 0L
        systemService!!.getDataDirectory(problemId)
    }

    /**
     * Test of getWorkDirectory method, of class SystemService.
     */
    @Test
    fun testGetWorkDirectory() {
        log.info("getWorkDirectory")
        val solutionId = 0L
        systemService!!.getWorkDirectory(solutionId)
    }

    /**
     * Test of getUploadDirectory method, of class SystemService.
     */
    @Test
    fun testGetUploadDirectory() {
        log.info("getUploadDirectory")
        systemService!!.uploadDirectory
    }

    companion object {
        private val log = LoggerFactory.getLogger(SystemServiceTest::class.java)
    }
}
