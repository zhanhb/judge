/*
 * Copyright 2016-2019 ZJNU ACM.
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
package cn.edu.zjnu.acm.judge.util

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.platform.runner.JUnitPlatform
import org.junit.runner.RunWith
import org.slf4j.LoggerFactory
import java.util.concurrent.ThreadLocalRandom

/**
 *
 * @author zhanhb
 */
@RunWith(JUnitPlatform::class)
class UtilityTest {

    /**
     * Test of getRandomString method, of class Utility.
     */
    @Test
    fun testGetRandomString() {
        log.info("getRandomString")
        var length = 8
        val result = Utility.getRandomString(length)
        assertThat(result.length).isEqualTo(length)
        for (i in 0..99) {
            length = ThreadLocalRandom.current().nextInt(35) + 6
            val t = Utility.getRandomString(length)
            assertThat(t.length).isEqualTo(length)
            assertThat(t.chars()).allMatch { ch -> Character.isLetterOrDigit(ch!!) }
        }
    }

    companion object {
        private val log = LoggerFactory.getLogger(UtilityTest::class.java)
    }
}
