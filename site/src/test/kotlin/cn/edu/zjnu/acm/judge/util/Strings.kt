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

/**
 *
 * @author zhanhb
 */
object Strings {

    fun slice(string: String?, beginIndex: Int): String? {
        return string?.substring(fixIndex(string, beginIndex))
    }

    fun slice(string: String?, beginIndex: Int, endIndex: Int): String? {
        if (string == null) {
            return null
        }
        val fixedBeginIndex = fixIndex(string, beginIndex)
        val fixedEndIndex = fixIndex(string, endIndex)
        return if (fixedBeginIndex >= fixedEndIndex) "" else string.substring(fixedBeginIndex, fixedEndIndex)
    }

    private fun fixIndex(str: String, index: Int): Int {
        return if (index >= 0) Math.min(str.length, index) else Math.max(0, str.length + index)
    }
}
