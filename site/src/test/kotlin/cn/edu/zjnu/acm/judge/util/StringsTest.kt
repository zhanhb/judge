/*
 * Copyright 2015 zhanhb.
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
import javax.script.ScriptEngineManager
import javax.script.ScriptException
import kotlin.streams.asSequence

/**
 *
 * @author zhanhb
 */
@RunWith(JUnitPlatform::class)
class StringsTest {

    // use null class loader, ensure access of system script engine manager.
    // usually our classloader will extends system class loader.
    // but surefire won't do like this when not forking
    // the enigine manager can be found though system class loader.
    private val javascript = ScriptEngineManager(null).getEngineByName("javascript")

    /**
     * Test of slice method, of class Strings.
     *
     * @throws javax.script.ScriptException
     */
    @Test
    @Throws(ScriptException::class)
    fun testSlice_String_int() {
        log.info("slice")

        val random = ThreadLocalRandom.current()
        val length = random.nextInt(5) + 20
        for (i in 0..29) {
            val randomString = random.ints(length.toLong(), 'a'.toInt(), 'z'.toInt() + 1)
                    .asSequence().joinToString("") {it.toChar().toString()}
            assertThat(randomString.length).isEqualTo(length)

            for (j in 0..29) {
                val start = random.nextInt(length * 6) - length * 3
                val result = Strings.slice(randomString, start)

                val expResult = javascript.eval("\'$randomString\'.slice($start)") as String
                assertThat(result).isEqualTo(expResult)
            }
        }
    }

    /**
     * Test of slice method, of class Strings.
     *
     * @throws javax.script.ScriptException
     */
    @Test
    @Throws(ScriptException::class)
    fun testSlice_3args() {
        log.info("slice")

        val random = ThreadLocalRandom.current()
        for (i in 0..29) {
            val length = random.nextInt(5) + 20
            val randomString = random.ints(length.toLong(), 'a'.toInt(), 'z'.toInt() + 1)
                    .asSequence().joinToString("") {it.toChar().toString()}
            assertThat(randomString.length).isEqualTo(length)

            for (j in 0..29) {
                val start = random.nextInt(length * 6) - length * 3
                val end = random.nextInt(length * 6) - length * 3
                val result = Strings.slice(randomString, start, end)

                val expResult = javascript.eval("\'$randomString\'.slice($start,$end)") as String
                assertThat(result).isEqualTo(expResult)
            }
        }
    }

    @Test
    fun testNull() {
        log.info("slice")
        assertThat(Strings.slice(null, 0)).isNull()
        assertThat(Strings.slice(null, 1)).isNull()
        assertThat(Strings.slice(null, -1000)).isNull()
        assertThat(Strings.slice(null, 0, 0)).isNull()
    }

    companion object {
        private val log = LoggerFactory.getLogger(StringsTest::class.java)
    }
}
