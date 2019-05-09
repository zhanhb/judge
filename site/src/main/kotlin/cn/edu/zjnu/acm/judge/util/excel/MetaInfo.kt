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
package cn.edu.zjnu.acm.judge.util.excel

import java.lang.reflect.Field
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap
import java.util.stream.Stream

/**
 *
 * @author zhanhb
 */
internal class MetaInfo private constructor(private val fieldMap: Map<String, Field>) {

    val headerAsStream: Stream<String>
        get() = fieldMap.keys.stream()

    val fieldsAsStream: Stream<Field>
        get() = fieldMap.values.stream()

    fun getField(name: String): Field? {
        return fieldMap[name]
    }

    fun size(): Int {
        return fieldMap.size
    }

    private data class Member(
            val name: String,
            val order: Int,
            val field: Field
    )

    companion object {

        private val MAP = ConcurrentHashMap<Locale, ConcurrentMap<Class<*>, MetaInfo>>(1)

        fun <T> forType(elementType: Class<T>, locale: Locale): MetaInfo {
            val metainfos = MAP.computeIfAbsent(locale) { ConcurrentHashMap(1) }
            return metainfos.computeIfAbsent(elementType) { type ->
                var bundle: ResourceBundle? = null
                try {
                    bundle = ResourceBundle.getBundle(type.name, locale, type.classLoader)
                } catch (ignore: MissingResourceException) {
                }

                val fields = elementType.declaredFields
                val list = ArrayList<Member>(fields.size)
                for (field in fields) {
                    val excel = field.getAnnotation(Excel::class.java)
                    if (excel != null) {
                        val key = excel.name
                        val order = excel.order
                        val name = try {
                            bundle?.getString(key) ?: key
                        } catch (ignore: MissingResourceException) {
                            key
                        }

                        field.isAccessible = true
                        list.add(Member(name, order, field))
                    }
                }
                // the map is created with LinkedHashMap in kotlin, which will preserve the order
                list.sortWith(Comparator.comparingInt<Member> { it.order }.thenComparing<String> { it.name })
                MetaInfo(list.associateBy({ it.name }, { it.field }))
            }
        }
    }

}
