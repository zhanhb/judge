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
package cn.edu.zjnu.acm.judge.util

import java.util.ArrayList
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort

/**
 *
 * @author zhanhb
 */
object Pageables {

    private fun buggy(): Pageable {
        val order = Sort.Order.desc("dummy")
        val list = ArrayList<Sort.Order>(1)
        list.add(order)
        val pageable = PageRequest.of(0, 50, Sort.by(list))
        list.clear()
        return pageable
    }

    fun bestSubmission(): Array<Pageable> {
        var sort = Sort.by(Sort.Direction.DESC, "time", "memory", "code_length")
        val a = PageRequest.of(5, 20, sort)
        sort = Sort.by(Sort.Direction.DESC, "solution_id")
        val b = PageRequest.of(5, 20, sort)
        val c = PageRequest.of(9, 1)
        val d = PageRequest.of(6, 21)
        return arrayOf(a, b, c, d, buggy())
    }

    fun users(): Array<Pageable> {
        val a = PageRequest.of(0, 50)

        val b = PageRequest.of(0, 50, Sort.by(Sort.Order.desc("solved"), Sort.Order.asc("submit")))
        return arrayOf(a, b, buggy())
    }

}
