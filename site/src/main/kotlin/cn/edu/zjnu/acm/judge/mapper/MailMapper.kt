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
package cn.edu.zjnu.acm.judge.mapper

import cn.edu.zjnu.acm.judge.data.dto.MailInfo
import cn.edu.zjnu.acm.judge.domain.Mail
import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.annotations.Param

/**
 *
 * @author zhanhb
 */
@Mapper
interface MailMapper {

    fun findOne(@Param("id") id: Long): Mail?

    fun findAllByTo(
            @Param("user") user: String,
            @Param("start") start: Long,
            @Param("size") size: Int): List<Mail>

    fun readed(@Param("id") id: Long): Long

    fun delete(id: Long): Long

    fun save(mail: Mail): Long

    fun getMailInfo(@Param("user") user: String): MailInfo?

    fun setReply(@Param("id") id: Long): Long

}
