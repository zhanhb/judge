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

import cn.edu.zjnu.acm.judge.util.PlatformAssuming.assumingWindows
import jnc.foreign.byref.AddressByReference
import jnc.platform.win32.Advapi32
import jnc.platform.win32.Kernel32Util
import jnc.platform.win32.SID
import jnc.platform.win32.WString
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.platform.runner.JUnitPlatform
import org.junit.runner.RunWith
import org.slf4j.LoggerFactory

/**
 *
 * @author zhanhb
 */
@RunWith(JUnitPlatform::class)
class IntegrityLevelTest {

    /**
     * Test of newPSID method, of class Advapi32Util.
     */
    @Test
    fun testNewPSID() {
        log.info("newPSID")
        for (integrityLevel in IntegrityLevel.values()) {
            val integrityLevelStr = integrityLevel.string ?: continue
            val pSid = AddressByReference()
            Kernel32Util.assertTrue(Advapi32.INSTANCE.ConvertStringSidToSidW(
                    WString.toNative(integrityLevelStr)!!, pSid))
            val sid: SID
            try {
                sid = SID.copyOf(pSid.value)
            } finally {
                Kernel32Util.freeLocalMemory(pSid.value)
            }
            assertTrue(sid.isValid)
            val sidString = sid.toString()
            assertThat(sidString).isEqualTo(integrityLevelStr)
        }
    }

    companion object {
        private val log = LoggerFactory.getLogger(IntegrityLevelTest::class.java)
        @JvmStatic
        @BeforeAll
        fun setUpClass() {
            assumingWindows()
        }
    }

}
