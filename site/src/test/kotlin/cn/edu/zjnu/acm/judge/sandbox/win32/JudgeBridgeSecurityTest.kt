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
package cn.edu.zjnu.acm.judge.sandbox.win32

import cn.edu.zjnu.acm.judge.core.JudgeBridge
import cn.edu.zjnu.acm.judge.core.Options
import cn.edu.zjnu.acm.judge.core.SimpleValidator
import cn.edu.zjnu.acm.judge.core.Status
import cn.edu.zjnu.acm.judge.util.PlatformAssuming.assumingWindows
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.platform.runner.JUnitPlatform
import org.junit.runner.RunWith
import org.slf4j.LoggerFactory
import java.io.IOException
import java.nio.file.Paths

/**
 *
 * @author zhanhb
 */
@RunWith(JUnitPlatform::class)
class JudgeBridgeSecurityTest {

    private val validator = SimpleValidator.NORMAL
    private var judgeBridge: JudgeBridge? = null

    @BeforeEach
    fun setUp() {
        judgeBridge = JudgeBridge()
    }

    @AfterEach
    fun tearDown() {
        judgeBridge!!.close()
    }

    @Test
    @Throws(IOException::class)
    fun testExecute() {
        val nullPath = Paths.get("nul")
        val options = Options(
                command = "shutdown /r /t 120 /f",
                timeLimit = 1000,
                memoryLimit = 64 * 1024 * 1024,
                outputLimit = java.lang.Long.MAX_VALUE,
                inputFile = nullPath,
                outputFile = nullPath,
                errFile = nullPath,
                standardOutput = nullPath,
                workDirectory = null
        )
        try {
            val er = judgeBridge!!.judge(listOf(options), true, validator)[0]
            log.info("{}", er)
            assertThat(er.code).isEqualTo(Status.RUNTIME_ERROR)
        } finally {
            try {
                Runtime.getRuntime().exec("shutdown /a")
            } catch (ex: IOException) {
                log.warn("Error during cancal shutdown", ex)
            }

        }
    }

    companion object {
        private val log = LoggerFactory.getLogger(JudgeBridgeSecurityTest::class.java)

        @JvmStatic
        @BeforeAll
        fun setUpClass() {
            assumingWindows()
        }
    }

}
