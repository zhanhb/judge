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
package com.github.zhanhb.judge.win32;

import jnr.ffi.Platform;
import jnr.ffi.byref.IntByReference;
import lombok.extern.slf4j.Slf4j;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.junit.Assume.assumeTrue;

/**
 *
 * @author zhanhb
 */
@Slf4j
public class Kernel32UtilTest {

    @BeforeClass
    public static void setUpClass() {
        assumeTrue("not windows", Platform.getNativePlatform().getOS() == Platform.OS.WINDOWS);
    }

    /**
     * Test of assertTrue method, of class Kernel32Util.
     */
    @Test
    public void testAssertTrue() {
        log.info("assertTrue");
        IntByReference dwExitCode = new IntByReference();
        try {
            Kernel32Util.assertTrue(Kernel32.INSTANCE.GetExitCodeProcess(0/*NULL*/, dwExitCode));
            fail("should throw a win32 exception");
        } catch (Win32Exception ex) {
            // invalid handle
            assertEquals(6, ex.getErrorCode());
        }
    }

}
