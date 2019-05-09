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

import cn.edu.zjnu.acm.judge.data.dto.ValueHolder
import cn.edu.zjnu.acm.judge.service.SystemService
import java.time.Instant
import org.springframework.http.HttpStatus
import org.springframework.security.access.annotation.Secured
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

import org.springframework.http.MediaType.APPLICATION_JSON_VALUE

/**
 *
 * @author zhanhb
 */
@RequestMapping(value = ["/api/system"], produces = [APPLICATION_JSON_VALUE])
@RestController
class SystemController(
        private val systemService: SystemService
) {

    @GetMapping("time")
    fun time(): Instant {
        return Instant.now()
    }

    @GetMapping("index")
    fun index(): ValueHolder<String> {
        return ValueHolder(systemService.index)
    }

    @PutMapping("index")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Secured("ROLE_ADMIN")
    fun setIndex(@RequestBody form: ValueHolder<String>) {
        systemService.index = form.value
    }

}
