/*
 * Copyright 2014 zhanhb.
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

import java.util.Random
import java.util.concurrent.ThreadLocalRandom

/**
 * @author zhanhb
 */
object Utility {

    @JvmOverloads
    fun getRandomString(length: Int, random: Random = ThreadLocalRandom.current()): String {
        val `val` = CharArray(length)
        for (i in 0 until length) {
            val x = random.nextInt(62)
            when ((x + 16) / 26) {
                0 -> `val`[i] = (x + '0'.toInt()).toChar()
                1 -> `val`[i] = (x + 'A'.toInt() - 10).toChar()
                2 -> `val`[i] = (x + 'a'.toInt() - 36).toChar()
            }
        }
        return String(`val`)
    }

}
