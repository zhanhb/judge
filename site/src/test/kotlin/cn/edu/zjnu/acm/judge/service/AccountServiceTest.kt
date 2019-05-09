/*
 * Copyright 2017 ZJNU ACM.
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

import cn.edu.zjnu.acm.judge.Application
import cn.edu.zjnu.acm.judge.data.form.AccountForm
import cn.edu.zjnu.acm.judge.util.Pageables
import org.junit.jupiter.api.Test
import org.junit.platform.runner.JUnitPlatform
import org.junit.runner.RunWith
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.web.WebAppConfiguration
import org.springframework.transaction.annotation.Transactional

/**
 *
 * @author zhanhb
 */
@RunWith(JUnitPlatform::class)
@SpringBootTest(classes = [Application::class])
@Transactional
@WebAppConfiguration
class AccountServiceTest {

    @Autowired
    private val accountService: AccountService? = null

    /**
     * Test of findAll method, of class AccountService.
     */
    @Test
    fun testFindAll() {
        log.info("findAll")
        for (pageable in Pageables.users()) {
            accountService!!.findAll(pageable)
            for (accountForm in buildForms()) {
                accountService.findAll(accountForm, pageable)
                accountService.findAll(accountForm.copy(disabled = true), pageable)
                accountService.findAll(accountForm.copy(disabled = false), pageable)
            }
        }
    }

    private fun buildForms(): Array<AccountForm> {
        val test = "test"
        return arrayOf(
                AccountForm(userId = test),
                AccountForm(nick = test),
                AccountForm(),
                AccountForm(nick = test, userId = test),
                AccountForm(query = "%"),
                AccountForm(query = test, nick = test)
        )
    }

    companion object {
        private val log = LoggerFactory.getLogger(AccountServiceTest::class.java)
    }
}
