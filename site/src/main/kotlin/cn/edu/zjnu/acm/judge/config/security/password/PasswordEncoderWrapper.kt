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
package cn.edu.zjnu.acm.judge.config.security.password;

import org.springframework.security.crypto.password.PasswordEncoder

/**
 *
 * @author zhanhb
 */
/* (non-Javadoc)
 * Class should be public for javadoc to access, link source option will generate link to source of this file.
 */
abstract class PasswordEncoderWrapper(private val encoder: PasswordEncoder) : PasswordEncoder {

    override fun encode(rawPassword: CharSequence): String {
        return encoder.encode(rawPassword)
    }

    override fun matches(rawPassword: CharSequence, encodedPassword: String): Boolean {
        return encoder.matches(rawPassword, encodedPassword)
    }

}
