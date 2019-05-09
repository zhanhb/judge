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
package cn.edu.zjnu.acm.judge.config.security.password

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory
import org.springframework.transaction.annotation.Transactional

/**
 *
 * @author zhanhb
 */
@Transactional
class MessageDigestPasswordEncoderTest {

    /**
     * Test of encode method, of class MessageDigestPasswordEncoder.
     */
    @Test
    fun testEncode() {
        log.info("encode")
        val password = "123456"
        @Suppress("DEPRECATION") val instance = MessageDigestPasswordEncoder.md5()
        val expResult = "e10adc3949ba59abbe56e057f20f883e"
        val result = instance.encode(password)
        assertThat(result).isEqualTo(expResult)
    }

    /**
     * Test of matches method, of class MessageDigestPasswordEncoder.
     */
    @Test
    fun testMatches() {
        log.info("matches")
        val rawPassword = ""
        val encodedPassword = "Da39a3ee5e6b4b0d3255bfef95601890afd80709"
        @Suppress("DEPRECATION") val instance = MessageDigestPasswordEncoder.sha1()
        val expResult = true
        val result = instance.matches(rawPassword, encodedPassword)
        assertThat(result).isEqualTo(expResult)
    }

    @Suppress("DEPRECATION")
    @Test
    fun testAll() {
        assertThat(arrayOf(
                MessageDigestPasswordEncoder.md5(),
                MessageDigestPasswordEncoder.sha1(),
                MessageDigestPasswordEncoder.sha256(),
                MessageDigestPasswordEncoder.sha384(),
                MessageDigestPasswordEncoder.sha512()
        )).allMatch { it.matches("test", it.encode("test")) }
    }

    companion object {
        private val log = LoggerFactory.getLogger(MessageDigestPasswordEncoderTest::class.java)
    }
}
