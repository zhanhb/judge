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
package cn.edu.zjnu.acm.judge.controller.api

import cn.edu.zjnu.acm.judge.Application
import cn.edu.zjnu.acm.judge.data.excel.Account
import cn.edu.zjnu.acm.judge.data.form.AccountImportForm
import cn.edu.zjnu.acm.judge.data.form.UserPasswordForm
import cn.edu.zjnu.acm.judge.domain.User
import cn.edu.zjnu.acm.judge.service.AccountService
import cn.edu.zjnu.acm.judge.service.MockDataService
import cn.edu.zjnu.acm.judge.util.Utility
import com.fasterxml.jackson.databind.ObjectMapper
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.platform.runner.JUnitPlatform
import org.junit.runner.RunWith
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.mock.web.MockMultipartFile
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.context.web.WebAppConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.MvcResult
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.transaction.annotation.Transactional
import java.util.*

/**
 *
 * @author zhanhb
 */
@AutoConfigureMockMvc
@RunWith(JUnitPlatform::class)
@SpringBootTest(classes = [Application::class])
@Transactional
@WebAppConfiguration
@WithMockUser(roles = ["ADMIN"])
class AccountControllerTest {

    @Autowired
    private val mvc: MockMvc? = null
    @Autowired
    private val objectMapper: ObjectMapper? = null
    @Autowired
    private val accountService: AccountService? = null
    @Autowired
    private val mockDataService: MockDataService? = null
    @Autowired
    private val passwordEncoder: PasswordEncoder? = null

    /**
     * Test of findAll method, of class AccountController.
     *
     * [AccountController.findAll]
     */
    @Test
    @Throws(Exception::class)
    fun testFindAll() {
        log.info("findAll")
        val userId = ""
        val nick = ""
        val query = ""
        val disabled: Boolean? = null
        val result = mvc!!.perform(get("/api/accounts.json")
                .param("userId", userId)
                .param("nick", nick)
                .param("query", query)
                .param("disabled", disabled?.toString() ?: ""))
                .andExpect(status().isOk)
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn()
    }

    /**
     * Test of update method, of class AccountController.
     *
     * [AccountController.update]
     */
    @Test
    @Throws(Exception::class)
    fun testUpdate() {
        log.info("update")
        var user = mockDataService!!.user()
        val userId = user.id
        assertThat(accountService!!.findOne(userId!!).school).isEqualTo(user.school)
        user = user.copy(school = "test school", password = "empty")
        mvc!!.perform(patch("/api/accounts/{userId}.json", userId)
                .content(objectMapper!!.writeValueAsString(user)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent)
                .andReturn()
        assertThat(accountService.findOne(userId).school).isEqualTo("test school")
        // password not changed, for annotation JsonIgnore is present on field password
        assertTrue(passwordEncoder!!.matches(userId, accountService.findOne(userId).password), "password should not be changed")
    }

    /**
     * Test of updatePassword method, of class AccountController.
     *
     * [AccountController.updatePassword]
     */
    @Test
    @Throws(Exception::class)
    fun testUpdatePassword() {
        log.info("updatePassword")
        val userId = mockDataService!!.user().id
        val form = UserPasswordForm()
        val newPassword = Utility.getRandomString(16)
        form.password = newPassword

        val result = mvc!!.perform(patch("/api/accounts/{userId}/password.json", userId!!)
                .content(objectMapper!!.writeValueAsString(form)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent)
                .andReturn()
        assertTrue(passwordEncoder!!.matches(newPassword, accountService!!.findOne(userId).password))
    }

    /**
     * Test of findAllXlsx method, of class AccountController.
     *
     * [AccountController.findAllXls]
     * [AccountController.parseExcel]
     */
    @Test
    @Throws(Exception::class)
    fun testFindAllXlsx() {
        log.info("findAllXlsx")
        test("/api/accounts.xlsx")
    }

    /**
     * Test of findAllXls method, of class AccountController.
     *
     * [AccountController.findAllXls]
     * [AccountController.parseExcel]
     */
    @Test
    @Throws(Exception::class)
    fun testFindAllXls() {
        log.info("findAllXls")
        test("/api/accounts.xls")
    }

    @Throws(Exception::class)
    private fun test0(url: String) {
        val content = mvc!!.perform(get(url))
                .andExpect(status().isOk)
                .andReturn().response.contentAsByteArray
        val file = MockMultipartFile("file", content)
        mvc.perform(multipart("/api/accounts.json").file(file))
                .andExpect(status().isOk)
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
    }

    @Throws(Exception::class)
    private fun test(url: String) {
        test0(url)
        mockDataService!!.user()
        test0(url)
    }

    /**
     * Test of importUsers method, of class AccountController.
     *
     * [AccountController.importUsers]
     */
    @Test
    @Throws(Exception::class)
    fun testImportUsers() {
        log.info("importUsers")
        val form = AccountImportForm()
        val account = toAccount(mockDataService!!.user(false))
        val account2 = toAccount(mockDataService.user(false))
        form.content = Arrays.asList(account, account2)
        expect(form, HttpStatus.NO_CONTENT)
        account.exists = true
        account2.exists = true
        expect(form, HttpStatus.BAD_REQUEST)
        form.existsPolicy = EnumSet.of(AccountImportForm.ExistPolicy.ENABLE)
        expect(form, HttpStatus.NO_CONTENT)
    }

    @Throws(Exception::class)
    private fun expect(form: AccountImportForm, status: HttpStatus): MvcResult {
        return mvc!!.perform(post("/api/accounts/import.json")
                .content(objectMapper!!.writeValueAsString(form)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().`is`(status.value()))
                .andReturn()
    }

    private fun toAccount(user: User): Account {
        val account = Account()
        account.id = user.id
        account.password = user.password
        account.email = user.email
        account.school = user.school
        return account
    }

    /**
     * Test of passwordStatus method, of class AccountController.
     *
     * [AccountController.passwordStatus]
     */
    @Test
    @Throws(Exception::class)
    fun testPasswordStatus() {
        log.info("passwordStatus")
        val result = mvc!!.perform(get("/api/accounts/password/status.json"))
                .andExpect(status().isOk)
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content").isMap)
                .andExpect(jsonPath("$.stats").isMap)
                .andReturn()
    }

    companion object {
        private val log = LoggerFactory.getLogger(AccountControllerTest::class.java)
    }
}
