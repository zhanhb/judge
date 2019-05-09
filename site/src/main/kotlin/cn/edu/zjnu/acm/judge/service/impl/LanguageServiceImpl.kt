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
package cn.edu.zjnu.acm.judge.service.impl

import cn.edu.zjnu.acm.judge.domain.Language
import cn.edu.zjnu.acm.judge.exception.BusinessCode
import cn.edu.zjnu.acm.judge.exception.BusinessException
import cn.edu.zjnu.acm.judge.mapper.LanguageMapper
import cn.edu.zjnu.acm.judge.service.LanguageService
import cn.edu.zjnu.acm.judge.util.SpecialCall
import org.springframework.stereotype.Service

/**
 *
 * @author zhanhb
 */
@Service("languageService")
@SpecialCall("contests/problems-status", "problems/status")
class LanguageServiceImpl(
        @Suppress("SpringJavaInjectionPointsAutowiringInspection") private val languageMapper: LanguageMapper
) : LanguageService {

    override val availableLanguages: Map<Int, Language>
        get() = languageMapper.findAll().associateBy({ it.id })

    override fun getAvailableLanguage(languageId: Int): Language {
        return languageMapper.findOne(languageId.toLong())
                ?: throw BusinessException(BusinessCode.LANGUAGE_NOT_FOUND, languageId)
    }

    @SpecialCall("contests/problems-status", "problems/status")
    override fun getLanguageName(languageId: Int): String {
        return languageMapper.findOne(languageId.toLong())?.name ?: "unknown language $languageId"
    }

}
