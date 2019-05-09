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

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.ser.std.StdSerializer
import java.io.IOException
import java.util.Locale
import org.springframework.boot.jackson.JsonComponent

/**
 *
 * @author zhanhb
 */
@JsonComponent
class CustomLocaleSerializer : StdSerializer<Locale>(Locale::class.java) {

    @Throws(IOException::class)
    override fun serialize(value: Locale, gen: JsonGenerator, serializers: SerializerProvider) {
        gen.writeString(value.toLanguageTag())
    }

    companion object {

        private const val serialVersionUID = 1L
    }

}
