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

/**
 * @author zhanhb
 */
class Win32Exception(val errorCode: Int) : RuntimeException() {

    override val message: String?
        get() = Kernel32Util.formatMessage(hresultFromWin32(errorCode))

    companion object {

        private val serialVersionUID = 1L

        private fun hresultFromWin32(x: Int): Int {
            return if (x <= 0) x else x and 0x0000FFFF or -0x7ff90000
        }
    }

}