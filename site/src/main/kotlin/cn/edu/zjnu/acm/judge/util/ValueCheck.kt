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

import cn.edu.zjnu.acm.judge.exception.BusinessCode
import cn.edu.zjnu.acm.judge.exception.BusinessException

/**
 * @author zhanhb
 */
object ValueCheck {

    const val EMAIL_PATTERN = "[a-z0-9!#$%&'*+/=?^_`{|}~-]++(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]++)*+@[a-z0-9](?:[a-z0-9-]*[a-z0-9])?+(?:\\.[a-z0-9](?:[a-z0-9-]*[a-z0-9])?+)++"

    fun checkUserId(userId: String?) {
        if (userId.isNullOrEmpty()) {
            throw BusinessException(BusinessCode.REGIST_USER_ID_EMPTY)
        }
        if (userId.length < 6) {
            throw BusinessException(BusinessCode.REGIST_USER_ID_SHORT)
        }
        if (userId.length > 20) {
            throw BusinessException(BusinessCode.REGIST_USER_ID_LONG)
        }
        if (!userId.matches("(?i)[a-z0-9_]+".toRegex())) {
            throw BusinessException(BusinessCode.REGIST_USER_ID_INVALID)
        }
    }

    fun checkPassword(password: String?) {
        if (password.isNullOrEmpty()) {
            throw BusinessException(BusinessCode.EMPTY_PASSWORD)
        }
        if (password.length > 20) {
            throw BusinessException(BusinessCode.PASSWORD_TOO_LONG)
        }
        if (password.length < 6) {
            throw BusinessException(BusinessCode.PASSWORD_TOO_SHORT)
        }
        for (i in 0 until password.length) {
            if (password[i] == ' ') {
                throw BusinessException(BusinessCode.PASSWORD_HAS_SPACE)
            }
        }
        for (i in 0 until password.length) {
            val ch = password[i]
            if (ch.toInt() >= 127 || ch.toInt() < 32) {
                throw BusinessException(BusinessCode.PASSWORD_INVALID_CHARACTER)
            }
        }
    }

    fun checkNick(nick: String?) {
        if (nick.isNullOrEmpty()) {
            throw BusinessException(BusinessCode.NICK_EMPTY)
        }
        if (nick.length > 64) {
            throw BusinessException(BusinessCode.NICK_LONG)
        }
    }

    fun checkEmail(email: String?) {
        if (!email.isNullOrEmpty() && !email.matches(EMAIL_PATTERN.toRegex())) {
            throw BusinessException(BusinessCode.EMAIL_FORMAT_INCORRECT)
        }
    }

}
