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

import cn.edu.zjnu.acm.judge.domain.DomainLocale
import java.util.Locale

/**
 *
 * @author zhanhb
 */
interface LocaleService {

    fun findAll(): List<DomainLocale>

    fun findOne(id: String): DomainLocale?

    fun resolve(locale: Locale?): String?

    fun support(all: Boolean): List<DomainLocale>

    fun toDomainLocale(locale: Locale, inLocale: Locale): DomainLocale

    fun toDomainLocale(localeName: String, supportOnly: Boolean): DomainLocale

    fun toSupported(locale: Locale?): Locale

}
