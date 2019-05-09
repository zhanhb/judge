/*
 * Copyright 2019 ZJNU ACM.
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

import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
import java.util.*

/**
 *
 * @author zhanhb
 */
enum class URLEncoder(dontNeedEncoding: String) {

    /**
     * https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/encodeURIComponent
     */
    URI_COMPONENT("!'()*-._~"),
    /**
     * pchar="!$&'()*+,-.:;=@_~"
     * https://tools.ietf.org/html/rfc3986#section-3.4
     */
    QUERY("!$'()*,-./:;?@_~"),
    PATH("!$'()*,-./:;@_~");

    private val dontNeedEncoding: BitSet

    init {
        val bs = BitSet(128)
        bs.set('a'.toInt(), 'z'.toInt() + 1)
        bs.set('A'.toInt(), 'Z'.toInt() + 1)
        bs.set('0'.toInt(), '9'.toInt() + 1)
        for (ch in dontNeedEncoding) {
            bs.set(ch.toInt())
        }
        this.dontNeedEncoding = bs
    }

    /**
     *
     * @param s `String` to be translated.
     * @param charset the encoding to use.
     * @return the translated `String`.
     * @see java.net.URLEncoder.encode
     * @throws NullPointerException s or charset is null
     */
    fun encode(s: String, charset: Charset): String {
        val length = s.length
        val bs = dontNeedEncoding

        var i = 0
        while (i < length) {
            if (!bs.get(s[i].toInt())) {
                val buf = charArrayOf('%', 0.toChar(), 0.toChar())
                val out = StringBuilder(length + 20).append(s, 0, i)
                var k = i
                while (true) {
                    do {
                        if (++k == length) {
                            return append(out, buf, s.substring(i).toByteArray(charset)).toString()
                        }
                    } while (!bs.get(s[k].toInt()))
                    append(out, buf, s.substring(i, k).toByteArray(charset))
                    i = k
                    do {
                        if (++k == length) {
                            return out.append(s, i, k).toString()
                        }
                    } while (bs.get(s[k].toInt()))
                    out.append(s, i, k)
                    i = k
                }
            }
            ++i
        }

        return s
    }

    /**
     *
     * @param s `String` to be translated.
     * @return the translated `String`.
     * @throws NullPointerException s is null
     */
    fun encode(s: String): String {
        return encode(s, StandardCharsets.UTF_8)
    }

    companion object {

        private val HEX_CHARS = "0123456789ABCDEF".toCharArray()

        private fun append(out: StringBuilder, buf: CharArray, bytes: ByteArray): StringBuilder {
            val hexChars = HEX_CHARS
            for (b in bytes) {
                buf[1] = hexChars[b.toInt().shr(4).and(15)]
                buf[2] = hexChars[b.toInt().and(15)]
                out.append(buf)
            }
            return out
        }
    }

}
