/*
 * Copyright 2018 ZJNU ACM.
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

import cn.edu.zjnu.acm.judge.sandbox.win32.IntegrityLevel.INTEGRITY_LEVEL_LAST
import cn.edu.zjnu.acm.judge.sandbox.win32.IntegrityLevel.INTEGRITY_LEVEL_LOW
import cn.edu.zjnu.acm.judge.sandbox.win32.TokenLevel.USER_LAST
import cn.edu.zjnu.acm.judge.util.PlatformAssuming.assumingWindows
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.Arguments.arguments
import org.junit.jupiter.params.provider.MethodSource
import org.junit.platform.runner.JUnitPlatform
import org.junit.runner.RunWith
import org.slf4j.LoggerFactory
import java.util.*

/**
 *
 * @author zhanhb
 */
@RunWith(JUnitPlatform::class)
class SandboxTest {

    /**
     * Test of createRestrictedToken method, of class Sandbox.
     */
    @ParameterizedTest
    @MethodSource("data")
    fun testCreateRestrictedToken(securityLevel: TokenLevel, integrityLevel: IntegrityLevel, tokenType: TokenType, lockdownDefaultDacl: Boolean) {
        log.info("CreateRestrictedToken")
        Handle.close(Sandbox.INSTANCE.createRestrictedToken(securityLevel, integrityLevel, tokenType, lockdownDefaultDacl))
    }

    companion object {
        private val log = LoggerFactory.getLogger(SandboxTest::class.java)

        @BeforeAll
        @JvmStatic
        fun setUpClass() {
            assumingWindows()
        }

        @JvmStatic
        fun data(): List<Arguments> {
            val list = ArrayList<Arguments>(56)
            val tokenLevels = TokenLevel.values()
            val integrityLevels = arrayOf(INTEGRITY_LEVEL_LOW, INTEGRITY_LEVEL_LAST)
            val tokenTypes = TokenType.values()
            val lockDowns = booleanArrayOf(true, false)
            for (securityLevel in tokenLevels) {
                if (USER_LAST === securityLevel) {
                    continue
                }
                for (integrityLevel in integrityLevels) {
                    for (tokenType in tokenTypes) {
                        for (lockdownDefaultDacl in lockDowns) {
                            list.add(arguments(securityLevel, integrityLevel, tokenType, lockdownDefaultDacl))
                        }
                    }
                }
            }
            return list
        }
    }

}
