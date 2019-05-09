/*
 * Copyright 2017-2019 ZJNU ACM.
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
package cn.edu.zjnu.acm.judge.service.impl

import cn.edu.zjnu.acm.judge.config.security.PasswordConfiguration
import cn.edu.zjnu.acm.judge.data.excel.Account
import cn.edu.zjnu.acm.judge.data.form.AccountForm
import cn.edu.zjnu.acm.judge.data.form.AccountImportForm
import cn.edu.zjnu.acm.judge.domain.User
import cn.edu.zjnu.acm.judge.exception.BusinessCode
import cn.edu.zjnu.acm.judge.exception.BusinessException
import cn.edu.zjnu.acm.judge.mapper.UserMapper
import cn.edu.zjnu.acm.judge.mapper.UserProblemMapper
import cn.edu.zjnu.acm.judge.mapper.UserRoleMapper
import cn.edu.zjnu.acm.judge.service.AccountService
import cn.edu.zjnu.acm.judge.util.EnumUtils
import cn.edu.zjnu.acm.judge.util.ValueCheck
import cn.edu.zjnu.acm.judge.util.excel.ExcelUtil
import com.google.common.annotations.VisibleForTesting
import org.springframework.context.MessageSource
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.util.CollectionUtils
import org.springframework.util.StringUtils
import java.io.InputStream
import java.time.Instant
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

/**
 *
 * @author zhanhb
 */
@Service("accountService")
class AccountServiceImpl(
        @Suppress("SpringJavaInjectionPointsAutowiringInspection") private val userMapper: UserMapper,
        private val passwordEncoder: PasswordEncoder,
        @Suppress("SpringJavaInjectionPointsAutowiringInspection") private val userRoleMapper: UserRoleMapper,
        @Suppress("SpringJavaInjectionPointsAutowiringInspection") private val userProblemMapper: UserProblemMapper,
        private val messageSource: MessageSource
) : AccountService {
    override fun findAll(form: AccountForm, pageable: Pageable): Page<User> {
        val list = userMapper.findAll(form, pageable)
        val count = userMapper.count(form)
        return PageImpl(list, pageable, count)
    }

    override fun findAllForExport(form: AccountForm, pageable: Pageable): List<Account> {
        return userMapper.findAllForExport(form, PageRequest.of(0, Integer.MAX_VALUE, pageable.sort))
    }

    override fun parseExcel(inputStream: InputStream, locale: Locale): List<Account> {
        val accounts = ExcelUtil.parse(inputStream, Account::class.java, locale)
                .filter { account -> StringUtils.hasText(account.id) }
        if (CollectionUtils.isEmpty(accounts)) {
            return accounts
        }
        val groupBy = accounts.groupBy { it.id!! }
        val exists = userMapper.findAllByUserIds(groupBy.keys)
        for (exist in exists) {
            for (account in groupBy[exist]!!) {
                account.exists = true
            }
        }
        return accounts
    }

    override fun findAll(pageable: Pageable): Page<User> {
        val form = AccountForm(disabled = false)
        return findAll(form, pageable)
    }

    override fun updateSelective(userId: String, user: User) {
        val copy = user.copy(
                createdTime = null,
                modifiedTime = Instant.now(),
                password = if (user.password != null) passwordEncoder.encode(user.password) else null
        )
        if (userMapper.updateSelective(userId, copy) == 0) {
            throw BusinessException(BusinessCode.USER_NOT_FOUND, userId)
        }
    }

    override fun updatePassword(userId: String, password: String) {
        if (userRoleMapper.countAdmin(userId) != 0L) {
            throw BusinessException(BusinessCode.RESET_PASSWORD_FORBIDDEN)
        }
        if (0 == userMapper.updateSelective(userId,
                        User(password = passwordEncoder.encode(password)))) {
            throw BusinessException(BusinessCode.USER_NOT_FOUND, userId)
        }
    }

    @Transactional
    override fun importUsers(form: AccountImportForm) {
        val accounts = form.content
        if (CollectionUtils.isEmpty(accounts)) {
            throw BusinessException(BusinessCode.IMPORT_USER_EMPTY)
        }
        val exists = accounts!!.filter { account -> java.lang.Boolean.TRUE == account.exists }
        if (!exists.isEmpty()) {
            val set = form.existsPolicy
            if (set.isEmpty()) {
                throw BusinessException(BusinessCode.IMPORT_USER_EXISTS)
            }
            val hasAdmin = userRoleMapper.countAdmin(*exists.map({ it.id!! }).toTypedArray()) != 0L
            if (hasAdmin && set.contains(AccountImportForm.ExistPolicy.RESET_PASSWORD)) {
                throw BusinessException(BusinessCode.IMPORT_USER_RESET_PASSWORD_FORBIDDEN)
            }
            val current = userMapper.countAllByUserIds(exists.map({ it.id!! }))
            if (current != exists.size.toLong()) {
                throw BusinessException(BusinessCode.IMPORT_USER_EXISTS_CHANGE)
            }
            prepare(exists)
            userMapper.batchUpdate(exists, EnumUtils.toMask(set))
        }
        val notExists = accounts
                .filter { account -> java.lang.Boolean.TRUE != account.exists }
        if (!notExists.isEmpty()) {
            val current = userMapper.countAllByUserIds(notExists.map({ it.id!! }))
            if (current != 0L) {
                throw BusinessException(BusinessCode.IMPORT_USER_EXISTS_CHANGE)
            }
            prepare(notExists)
            userMapper.insert(notExists)
        }
    }

    private fun prepare(accounts: Collection<Account>) {
        for (account in accounts) {
            if (account.id.isNullOrEmpty()) {
                throw BusinessException(BusinessCode.IMPORT_USER_ID_EMPTY)
            }
            val password = account.password
            if (password.isNullOrEmpty()) {
                throw BusinessException(BusinessCode.EMPTY_PASSWORD)
            }
            if (password.length <= PasswordConfiguration.MAX_PASSWORD_LENGTH) {
                ValueCheck.checkPassword(password)
                account.password = passwordEncoder.encode(password)
            }
            if (account.email.isNullOrBlank()) {
                account.email = null
            }
            if (account.nick.isNullOrBlank()) {
                account.nick = account.id
            }
            if (account.school == null) {
                account.school = ""
            }
        }
    }

    override fun save(user: User) {
        userMapper.save(user)
    }

    override fun findOne(id: String): User {
        return userMapper.findOne(id) ?: throw BusinessException(BusinessCode.USER_NOT_FOUND, id)
    }

    @Transactional
    @VisibleForTesting
    override fun delete(id: String) {
        val result = userProblemMapper.deleteByUser(id) + userMapper.delete(id)
        if (result == 0L) {
            throw BusinessException(BusinessCode.USER_NOT_FOUND, id)
        }
    }

    override fun getExcelName(locale: Locale): String {
        val name = messageSource.getMessage("onlinejudge.export.excel.name", arrayOfNulls<Any>(0), locale)
        return name + " - " + dtf.format(LocalDateTime.now())
    }

    companion object {
        private val dtf = DateTimeFormatter.ISO_DATE
    }
}
