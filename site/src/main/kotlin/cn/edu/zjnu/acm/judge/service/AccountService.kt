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
package cn.edu.zjnu.acm.judge.service

import cn.edu.zjnu.acm.judge.data.excel.Account
import cn.edu.zjnu.acm.judge.data.form.AccountForm
import cn.edu.zjnu.acm.judge.data.form.AccountImportForm
import cn.edu.zjnu.acm.judge.domain.User
import java.io.InputStream
import java.util.Locale
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

/**
 *
 * @author zhanhb
 */
interface AccountService {

    fun delete(id: String)

    fun findAll(form: AccountForm, pageable: Pageable): Page<User>

    /**
     * find all users not disabled
     */
    fun findAll(pageable: Pageable): Page<User>

    fun findAllForExport(form: AccountForm, pageable: Pageable): List<Account>

    fun findOne(id: String): User

    fun importUsers(form: AccountImportForm)

    fun parseExcel(inputStream: InputStream, locale: Locale): List<Account>

    fun save(user: User)

    fun updatePassword(userId: String, password: String)

    fun updateSelective(userId: String, user: User)

    fun getExcelName(locale: Locale): String

}
