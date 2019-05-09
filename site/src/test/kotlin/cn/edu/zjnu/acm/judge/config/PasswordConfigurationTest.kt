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
package cn.edu.zjnu.acm.judge.config

import cn.edu.zjnu.acm.judge.Application
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.platform.runner.JUnitPlatform
import org.junit.runner.RunWith
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.crypto.password.PasswordEncoder
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
class PasswordConfigurationTest {

    @Autowired
    private val passwordEncoder: PasswordEncoder? = null

    @Test
    fun testMatches() {
        log.info("matches")
        val rawPassword = "123456"
        var encodedPassword = "123456"
        val md5 = "7c4a8d09ca3762af61e59520943dc26494f8941b"
        assertTrue(passwordEncoder!!.matches(rawPassword, encodedPassword))
        encodedPassword = md5
        assertTrue(passwordEncoder.matches(rawPassword, encodedPassword))
        encodedPassword = "123456,123456"
        assertFalse(passwordEncoder.matches(rawPassword, encodedPassword))
        val wrong = md5.substring(0, md5.length - 3) + "ttt"
        encodedPassword = String.format("%s,%s", md5, wrong)
        assertTrue(passwordEncoder.matches(rawPassword, encodedPassword))
        encodedPassword = String.format("%s,%s", wrong, md5)
        assertTrue(passwordEncoder.matches(rawPassword, encodedPassword))
        encodedPassword = String.format("%s,%s", wrong, wrong)
        assertFalse(passwordEncoder.matches(rawPassword, encodedPassword))
        var p = md5
        assertFalse(passwordEncoder.matches(p, p))
        p = p.substring(0, 20)
        assertTrue(passwordEncoder.matches(p, p))
        encodedPassword = String.format("%s,%s", rawPassword, rawPassword)
        assertFalse(passwordEncoder.matches(rawPassword, encodedPassword))
    }

    @Test
    fun testEncode() {
        log.info("passwordEncoder")
        val rawPassword = "123456"
        val result = passwordEncoder!!.encode(rawPassword)
        assertTrue(passwordEncoder.matches(rawPassword, result))

    }

    companion object {
        private val log = LoggerFactory.getLogger(PasswordConfigurationTest::class.java)
    }
}
