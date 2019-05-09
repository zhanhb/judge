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

import cn.edu.zjnu.acm.judge.data.excel.Account
import cn.edu.zjnu.acm.judge.data.form.AccountForm
import cn.edu.zjnu.acm.judge.data.form.AccountImportForm
import cn.edu.zjnu.acm.judge.data.form.UserPasswordForm
import cn.edu.zjnu.acm.judge.domain.User
import cn.edu.zjnu.acm.judge.service.AccountService
import cn.edu.zjnu.acm.judge.service.ResetPasswordService
import cn.edu.zjnu.acm.judge.util.CustomMediaType.XLSX_VALUE
import cn.edu.zjnu.acm.judge.util.CustomMediaType.XLS_VALUE
import cn.edu.zjnu.acm.judge.util.excel.ExcelType
import cn.edu.zjnu.acm.judge.util.excel.ExcelUtil
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE
import org.springframework.security.access.annotation.Secured
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.io.IOException
import java.util.Locale
import javax.servlet.http.HttpServletResponse

/**
 *
 * @author zhanhb
 */
@RequestMapping(value = ["/api/accounts"], produces = [APPLICATION_JSON_VALUE])
@RestController
@Secured("ROLE_ADMIN")
class AccountController(
        private val accountService: AccountService,
        private val resetPasswordService: ResetPasswordService
) {

    @GetMapping
    fun findAll(form: AccountForm, @PageableDefault(50) pageable: Pageable): Page<User> {
        return accountService.findAll(form, pageable)
    }

    @PatchMapping("{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun update(@PathVariable("userId") userId: String, @RequestBody user: User) {
        accountService.updateSelective(userId, user.copy(password = null))
    }

    @PatchMapping("{userId}/password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun updatePassword(@PathVariable("userId") userId: String, @RequestBody user: UserPasswordForm) {
        accountService.updatePassword(userId, user.password!!)
    }

    @GetMapping(produces = [XLSX_VALUE])
    @Throws(IOException::class)
    fun findAllXlsx(form: AccountForm, pageable: Pageable, requestLocale: Locale?,
                    response: HttpServletResponse) {
        val locale = requestLocale ?: Locale.ROOT
        val content = accountService.findAllForExport(form, pageable)
        ExcelUtil.toResponse(Account::class.java, content, locale, ExcelType.XLSX,
                accountService.getExcelName(locale), response)
    }

    @GetMapping(produces = [XLS_VALUE])
    @Throws(IOException::class)
    fun findAllXls(form: AccountForm, pageable: Pageable, requestLocale: Locale?,
                   response: HttpServletResponse) {
        val locale = requestLocale ?: Locale.ROOT
        val content = accountService.findAllForExport(form, pageable)
        ExcelUtil.toResponse(Account::class.java, content, locale, ExcelType.XLS,
                accountService.getExcelName(locale), response)
    }

    @PostMapping(consumes = [MULTIPART_FORM_DATA_VALUE])
    @Throws(IOException::class)
    fun parseExcel(@RequestParam("file") multipartFile: MultipartFile,
                   locale: Locale?): List<Account> {
        multipartFile.inputStream.use { inputStream ->
            return accountService.parseExcel(inputStream, locale ?: Locale.ROOT)
        }
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PostMapping(value = ["import"], consumes = [APPLICATION_JSON_VALUE])
    fun importUsers(@RequestBody form: AccountImportForm) {
        accountService.importUsers(form)
    }

    @GetMapping("password/status")
    fun passwordStatus(): Map<String, *> {
        return resetPasswordService.stats()
    }

}
