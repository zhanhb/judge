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
package cn.edu.zjnu.acm.judge.config.security.password

import com.google.common.hash.HashFunction
import com.google.common.hash.Hashing
import org.springframework.security.crypto.password.PasswordEncoder
import java.nio.charset.StandardCharsets

/**
 *
 * @author zhanhb
 */

class MessageDigestPasswordEncoder private constructor(private val action: () -> HashFunction) : PasswordEncoder {

    override fun encode(password: CharSequence): String {
        return action().hashString(password, StandardCharsets.UTF_8).toString()
    }

    override fun matches(rawPassword: CharSequence, encodedPassword: String): Boolean {
        return action().bits().ushr(2) == encodedPassword.length && this.encode(rawPassword).equals(encodedPassword, ignoreCase = true)
    }

    private object SHA512Holder {
        val INSTANCE = MessageDigestPasswordEncoder { Hashing.sha512() }
    }

    private object SHA384Holder {
        val INSTANCE = MessageDigestPasswordEncoder { Hashing.sha384() }
    }

    private object SHA256Holder {
        val INSTANCE = MessageDigestPasswordEncoder { Hashing.sha256() }
    }

    @Deprecated("")
    private object SHAHolder {
        val INSTANCE = MessageDigestPasswordEncoder { Hashing.sha1() }
    }

    @Deprecated("")
    private object MD5Holder {
        val INSTANCE = MessageDigestPasswordEncoder { Hashing.md5() }
    }

    companion object {

        @Deprecated("")
        fun md5(): MessageDigestPasswordEncoder {
            return MD5Holder.INSTANCE
        }

        @Deprecated("")
        fun sha1(): MessageDigestPasswordEncoder {
            return SHAHolder.INSTANCE
        }

        fun sha256(): MessageDigestPasswordEncoder {
            return SHA256Holder.INSTANCE
        }

        fun sha384(): MessageDigestPasswordEncoder {
            return SHA384Holder.INSTANCE
        }

        fun sha512(): MessageDigestPasswordEncoder {
            return SHA512Holder.INSTANCE
        }
    }
}
