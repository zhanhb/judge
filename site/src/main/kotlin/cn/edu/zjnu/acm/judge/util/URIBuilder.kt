/*
 * Copyright 2016-2019 ZJNU ACM.
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
package cn.edu.zjnu.acm.judge.util

import com.google.common.collect.Maps
import javax.servlet.http.HttpServletRequest

/**
 *
 * @author zhanhb
 */
class URIBuilder private constructor(private var path: String?, query: Map<String, Array<String>>) {
    private val query: MutableMap<String, Array<String>>

    init {
        this.query = Maps.newLinkedHashMapWithExpectedSize(query.size)
        query.forEach({ (name, values) -> this.replaceQueryParam(name, *values) })
    }

    fun replaceQueryParam(name: String, vararg values: String): URIBuilder {
        if (values.isEmpty()) {
            query.remove(name)
        } else {
            query[name] = arrayOf(*values)
        }
        return this
    }

    fun replacePath(path: String?): URIBuilder {
        this.path = path ?: ""
        return this
    }

    override fun toString(): String {
        val it = query.entries.iterator()
        val value = URLEncoder.PATH.encode(path!!)
        if (!it.hasNext()) {
            return value
        }
        val qe = URLEncoder.QUERY
        val sb = StringBuilder(value)
        var ch = '?'
        while (true) {
            val entry = it.next()
            val key = qe.encode(entry.key)
            for (`val` in entry.value) {
                sb.append(ch).append(key).append('=').append(qe.encode(`val`))
                ch = '&'
            }
            if (!it.hasNext()) {
                return sb.toString()
            }
        }
    }

    companion object {

        fun fromRequest(request: HttpServletRequest): URIBuilder {
            return URIBuilder(request.servletPath, request.parameterMap)
        }
    }

}
