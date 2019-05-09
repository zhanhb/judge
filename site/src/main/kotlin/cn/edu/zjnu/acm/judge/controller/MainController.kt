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
package cn.edu.zjnu.acm.judge.controller

import cn.edu.zjnu.acm.judge.config.JudgeHandlerInterceptor
import cn.edu.zjnu.acm.judge.service.ContestOnlyService
import cn.edu.zjnu.acm.judge.service.SystemService
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.http.MediaType.TEXT_HTML_VALUE
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.servlet.mvc.support.RedirectAttributes
import javax.servlet.http.HttpServletRequest

/**
 *
 * @author zhanhb
 */
@Controller
@RequestMapping(produces = [TEXT_HTML_VALUE])
class MainController(
        private val contestOnlyService: ContestOnlyService,
        private val systemService: SystemService
) {

    @GetMapping("/")
    fun index(model: Model): String {
        model.addAttribute("content", systemService.index)
        return "index"
    }

    @GetMapping("faq")
    fun faq(): String {
        return "faq"
    }

    @GetMapping("findpassword")
    fun findPassword(): String {
        return "users/findPassword"
    }

    @GetMapping("registerpage", "register")
    fun registerPage(): String {
        contestOnlyService.checkRegister()
        return "users/registerPage"
    }

}
