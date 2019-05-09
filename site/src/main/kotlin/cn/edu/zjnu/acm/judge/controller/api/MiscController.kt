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
package cn.edu.zjnu.acm.judge.controller.api

import cn.edu.zjnu.acm.judge.data.dto.ValueHolder
import cn.edu.zjnu.acm.judge.data.form.SystemInfoForm
import cn.edu.zjnu.acm.judge.mapper.UserProblemMapper
import cn.edu.zjnu.acm.judge.service.ContestOnlyService
import cn.edu.zjnu.acm.judge.service.SystemService
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.security.access.annotation.Secured
import org.springframework.web.bind.annotation.*
import java.util.concurrent.CompletableFuture

/**
 *
 * @author zhanhb
 */
@RequestMapping(value = ["/api/misc"], produces = [APPLICATION_JSON_VALUE])
@RestController
@Secured("ROLE_ADMIN")
class MiscController(
        @Suppress("SpringJavaInjectionPointsAutowiringInspection") private val userProblemMapper: UserProblemMapper,
        private val systemService: SystemService,
        private val contestOnlyService: ContestOnlyService
) {
    @PostMapping("fix")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun fix(): CompletableFuture<*> {
        userProblemMapper.init()
        val b = CompletableFuture.runAsync { userProblemMapper.updateProblems() }
        val c = CompletableFuture.runAsync { userProblemMapper.updateUsers() }
        return CompletableFuture.allOf(b, c)
    }

    @PutMapping("systemInfo")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun setSystemInfo(@RequestBody json: SystemInfoForm) {
        val info = json.info
        val pureText = json.pureText
        val systemInfo: SystemInfoForm?
        if (info.isNullOrEmpty()) {
            systemInfo = null
        } else {
            systemInfo = SystemInfoForm(pureText = pureText, info = info.trim())
        }
        systemService.systemInfo = systemInfo
    }

    @GetMapping("systemInfo")
    fun systemInfo(): SystemInfoForm {
        var systemInfo = systemService.systemInfo
        if (systemInfo == null) {
            systemInfo = SystemInfoForm(info = null, pureText = true)
        } else if (systemInfo.info.isNullOrEmpty()) {
            systemInfo.pureText = true
        }
        return systemInfo
    }

    @GetMapping("contestOnly")
    fun contestOnly(): ValueHolder<Long> {
        return ValueHolder(contestOnlyService.contestOnly)
    }

    @PutMapping("contestOnly")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun setContestOnly(@RequestBody form: ValueHolder<Long>) {
        contestOnlyService.contestOnly = form.value
    }

}
