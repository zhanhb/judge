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

import jnc.foreign.annotation.Pack

/**
 * @see [LUID_AND_ATTRIBUTES](https://msdn.microsoft.com/en-us/library/windows/desktop/aa379263)
 * @author zhanhb
 */
@Pack(4)
class LUID_AND_ATTRIBUTES : jnc.foreign.Struct() {

    val luid: LUID = inner(LUID())
    private val Attributes = DWORD()

    var attributes: Int
        get() = Attributes.get().toInt()
        set(attributes) = Attributes.set(attributes.toLong())

}