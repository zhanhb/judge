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

import cn.edu.zjnu.acm.judge.domain.Language
import cn.edu.zjnu.acm.judge.mapper.LanguageMapper
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.security.access.annotation.Secured
import org.springframework.web.bind.annotation.*

/**
 *
 * @author zhanhb
 */
@RequestMapping(value = ["/api/languages"], produces = [APPLICATION_JSON_VALUE])
@RestController
@Secured("ROLE_ADMIN")
class LanguageController(
        @Suppress("SpringJavaInjectionPointsAutowiringInspection") private val languageMapper: LanguageMapper
) {

    @GetMapping
    fun findAll(): List<Language> {
        return languageMapper.findAll()
    }

    @PostMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun save(@RequestBody language: Language) {
        languageMapper.save(language)
    }

    @GetMapping("{id}")
    fun findOne(@PathVariable("id") id: Long): Language? {
        return languageMapper.findOne(id)
    }

    @PutMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun update(@PathVariable("id") id: Long, @RequestBody language: Language) {
        languageMapper.update(id, language)
    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun delete(@PathVariable("id") id: Long) {
        languageMapper.deleteById(id)
    }

}
