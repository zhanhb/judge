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

import jnc.platform.win32.Kernel32
import jnc.platform.win32.WinBase.SEM_FAILCRITICALERRORS
import jnc.platform.win32.WinBase.SEM_NOGPFAULTERRORBOX
import jnc.platform.win32.WinBase.SEM_NOOPENFILEERRORBOX

/**
 *
 * @author zhanhb
 */
object ProcessCreationHelper {

    fun <V> execute(supplier: () -> V): V {
        synchronized(Runtime.getRuntime()) {
            val oldErrorMode = Kernel32.INSTANCE.SetErrorMode(SEM_FAILCRITICALERRORS or SEM_NOGPFAULTERRORBOX or SEM_NOOPENFILEERRORBOX)
            try {
                return supplier()
            } finally {
                Kernel32.INSTANCE.SetErrorMode(oldErrorMode)
            }
        }
    }

    fun execute(runnable: () -> Unit) {
        execute<Int> { runnable();0 }
    }

}
