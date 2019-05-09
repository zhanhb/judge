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
package cn.edu.zjnu.acm.judge.config.jackson

import cn.edu.zjnu.acm.judge.Application
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.gson.Gson
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.platform.runner.JUnitPlatform
import org.junit.runner.RunWith
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.web.WebAppConfiguration
import java.io.IOException
import java.util.*

/**
 *
 * @author zhanhb
 */
@RunWith(JUnitPlatform::class)
@SpringBootTest(classes = [Application::class])
@WebAppConfiguration
class LocaleSerializeTest {

    @Autowired
    private val objectMapper: ObjectMapper? = null

    @Test
    @Throws(IOException::class)
    fun testToJson() {
        for (expect in arrayOf(
                Locale.US,
                Locale.forLanguageTag("zh-TW-x-lvariant-Hant"),
                Locale.TAIWAN,
                Locale("zh", "TW", "Hant"),
                Locale.ENGLISH,
                Locale.ROOT,
                null
        )) {
            var str = objectMapper!!.writeValueAsString(expect)
            val tag = expect?.toLanguageTag()
            val expectStr = Gson().toJson(tag)
            Assertions.assertThat(str).isEqualTo(expectStr)
            var result: Locale? = objectMapper.readValue(str, Locale::class.java)
            log.info("str={}, expect={}, result={}", str, expect, result)
            assertThat(result).isEqualTo(expect)

            str = objectMapper.writeValueAsString(LocaleHolder(expect))
            log.info("str={}", str)

            result = objectMapper.readValue(str, LocaleHolder::class.java).locale
            assertThat(result).isEqualTo(expect)
        }
    }

    companion object {
        private val log = LoggerFactory.getLogger(LocaleSerializeTest::class.java)
    }
}
