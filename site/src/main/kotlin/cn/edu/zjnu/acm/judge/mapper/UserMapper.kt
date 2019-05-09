/*
 * Copyright 2015 ZJNU ACM.
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
package cn.edu.zjnu.acm.judge.mapper

import cn.edu.zjnu.acm.judge.data.excel.Account
import cn.edu.zjnu.acm.judge.data.form.AccountForm
import cn.edu.zjnu.acm.judge.data.form.AccountImportForm
import cn.edu.zjnu.acm.judge.domain.User
import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.annotations.Param
import org.springframework.data.domain.Pageable

/**
 *
 * @author zhanhb
 */
@Mapper
interface UserMapper {

    fun findOne(@Param("id") id: String): User?

    fun save(user: User): Long

    fun count(@Param("form") form: AccountForm): Long

    fun rank(@Param("id") userId: String): Long

    fun neighbours(@Param("id") userId: String, @Param("c") count: Int): List<User>

    fun recentrank(@Param("count") count: Int): List<User>

    fun findAll(@Param("form") form: AccountForm, @Param("pageable") pageable: Pageable): List<User>

    fun findAllForExport(@Param("form") form: AccountForm, @Param("pageable") pageable: Pageable): List<Account>

    fun updateSelective(@Param("userId") userId: String, @Param("user") user: User): Int

    fun findAllByUserIds(@Param("userIds") userIds: Collection<String>): List<String>

    fun countAllByUserIds(@Param("userIds") userIds: Collection<String>): Long

    /**
     * [AccountImportForm.ExistPolicy]
     */
    fun batchUpdate(@Param("accounts") accounts: List<Account>, @Param("mask") mask: Int): Int

    fun insert(@Param("accounts") accounts: List<Account>): Int

    fun delete(userId: String): Int

}
