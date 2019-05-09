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
package jnc.platform.win32

import cn.edu.zjnu.acm.judge.util.PlatformAssuming.assumingWindows
import jnc.foreign.byref.IntByReference
import jnc.platform.win32.WinError.ERROR_INVALID_HANDLE
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.platform.runner.JUnitPlatform
import org.junit.runner.RunWith
import org.slf4j.LoggerFactory

/**
 * @author zhanhb
 */
@RunWith(JUnitPlatform::class)
class Kernel32UtilTest {

    /**
     * Test of assertTrue method, of class Kernel32Util.
     */
    @Test
    fun testAssertTrue() {
        log.info("assertTrue")
        val dwExitCode = IntByReference()
        val win32Exception = assertThrows(Win32Exception::class.java) { Kernel32Util.assertTrue(Kernel32.INSTANCE.GetExitCodeProcess(0/*NULL*/, dwExitCode)) }
        // invalid handle
        assertThat(win32Exception.errorCode).isEqualTo(ERROR_INVALID_HANDLE)
        assertThat(win32Exception.message).isNotBlank()
    }

    companion object {
        private val log = LoggerFactory.getLogger(Kernel32UtilTest::class.java)

        @JvmStatic
        @BeforeAll
        fun setUpClass() {
            assumingWindows()
        }
    }

}
