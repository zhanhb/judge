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
package cn.edu.zjnu.acm.judge.controller.api

import cn.edu.zjnu.acm.judge.domain.DomainLocale
import cn.edu.zjnu.acm.judge.service.LocaleService
import org.springframework.security.access.annotation.Secured
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import java.util.Locale

/**
 *
 * @author zhanhb
 */
@RequestMapping(value = ["/api/locales"], produces = [APPLICATION_JSON_VALUE])
@RestController
@Secured("ROLE_ADMIN")
class LocaleController(
        private val localeService: LocaleService
) {

    @GetMapping("current")
    fun current(requestLocale: Locale?): DomainLocale {
        val locale = requestLocale ?: Locale.ROOT
        return localeService.toDomainLocale(localeService.toSupported(locale), locale)
    }

    @GetMapping("{id}")
    fun findOne(@PathVariable("id") name: String,
                @RequestParam(value = "support", required = false, defaultValue = "false") support: Boolean): DomainLocale {
        return localeService.toDomainLocale(name, support)
    }

    @GetMapping
    fun findAll(): List<DomainLocale> {
        return localeService.findAll()
    }

    @GetMapping(params = ["all"])
    fun supported(@RequestParam("all") all: Boolean): List<DomainLocale> {
        return localeService.support(all)
    }

}
