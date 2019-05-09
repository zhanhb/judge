/*
 * Copyright 2016 ZJNU ACM.
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
package cn.edu.zjnu.acm.judge.mapper

import cn.edu.zjnu.acm.judge.domain.Message
import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.annotations.Param

/**
 *
 * @author zhanhb
 */
@Mapper
interface MessageMapper {

    @Deprecated("")
    fun nextId(): Long

    fun findOne(@Param("id") id: Long): Message?

    fun findAllByThreadIdAndOrderNumGreaterThanOrderByOrderNum(
            @Param("thread") thread: Long, @Param("order") order: Long): List<Message>

    fun updateOrderNumByThreadIdAndOrderNumGreaterThan(@Param("threadId") thread: Long, @Param("order") order: Long): Long

    fun updateThreadIdByThreadId(@Param("threadId") nextId: Long, @Param("original") original: Long): Long

    fun save(@Param("id") id: Long,
             @Param("parentId") parentId: Long?,
             @Param("order") order: Long,
             @Param("problemId") problemId: Long?,
             @Param("depth") depth: Long,
             @Param("userId") userId: String,
             @Param("title") title: String,
             @Param("content") content: String): Long

    fun findAllByThreadIdBetween(
            @Param("min") min: Long?, // inclusive
            @Param("max") max: Long?, // exclude
            @Param("problemId") problemId: Long?,
            @Param("limit") limit: Int?): List<Message>

    fun mint(
            @Param("top") top: Long,
            @Param("problemId") problemId: Long?,
            @Param("limit") limit: Int,
            @Param("coalesce") coalesce: Long): Long

    fun maxt(@Param("top") top: Long, @Param("problemId") problemId: Long?, @Param("limit") limit: Int, @Param("coalesce") coalesce: Long): Long

}
