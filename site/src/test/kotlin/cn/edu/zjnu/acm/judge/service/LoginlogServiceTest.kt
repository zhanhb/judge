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
import cn.edu.zjnu.acm.judge.domain.LoginLog
import cn.edu.zjnu.acm.judge.service.impl.LoginlogServiceImpl
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.platform.runner.JUnitPlatform
import org.junit.runner.RunWith
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.web.WebAppConfiguration
import org.springframework.transaction.annotation.Transactional
import java.util.*
import java.util.concurrent.TimeUnit

/**
 *
 * @author zhanhb
 */
@RunWith(JUnitPlatform::class)
@SpringBootTest(classes = [Application::class])
@Transactional
@WebAppConfiguration
class LoginlogServiceTest {

    @Autowired
    private val loginlogService: LoginlogService? = null

    @AfterEach
    @Throws(InterruptedException::class)
    fun tearDown() {
        val impl = loginlogService as LoginlogServiceImpl?
        impl!!.destroy()
        assertTrue(impl.await(120, TimeUnit.SECONDS))
    }

    /**
     * Test of save method, of class LoginlogService.
     *
     * @throws java.lang.InterruptedException
     */
    @Test
    @Throws(InterruptedException::class)
    fun testSave() {
        log.info("save")
        for (i in 0..9999) {
            val loginlog = LoginLog(user = uuid(), password = uuid(), ip = "127.0.0.1")
            loginlogService!!.save(loginlog)
        }
        log.info("finish")
    }

    private fun uuid(): String {
        return UUID.randomUUID().toString().replace("-", "").toLowerCase()
    }

    companion object {
        private val log = LoggerFactory.getLogger(LoginlogServiceTest::class.java)
    }
}
