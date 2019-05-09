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
package cn.edu.zjnu.acm.judge.controller.api

import cn.edu.zjnu.acm.judge.data.form.ProblemForm
import cn.edu.zjnu.acm.judge.domain.Problem
import cn.edu.zjnu.acm.judge.service.ProblemService
import com.google.common.collect.ImmutableMap
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.security.access.annotation.Secured
import org.springframework.web.bind.annotation.*
import java.io.IOException
import java.util.Locale

/**
 *
 * @author zhanhb
 */
@RequestMapping(value = ["/api/problems"], produces = [APPLICATION_JSON_VALUE])
@RestController
@Secured("ROLE_ADMIN")
class ProblemController(
        private val problemService: ProblemService
) {
    @PostMapping
    fun save(@RequestBody problem: Problem): Problem {
        return problemService.save(problem)
    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun delete(@PathVariable("id") id: Long) {
        problemService.delete(id)
    }

    @GetMapping("{id}/dataDir")
    @Throws(IOException::class)
    fun dataDir(@PathVariable("id") id: Long): Map<String, String> {
        val dataDir = problemService.getDataDirectory(id).toString()
        return ImmutableMap.of("dataDir", dataDir)
    }

    @GetMapping("{id}")
    fun findOne(@PathVariable("id") id: Long,
                @RequestParam(value = "locale", required = false) lang: String?): Problem {
        return problemService.findOne(id, lang)
    }

    @GetMapping
    fun list(problemForm: ProblemForm, @PageableDefault(100) pageable: Pageable, locale: Locale): Page<Problem> {
        log.info("pageable: {}", pageable)
        return problemService.findAll(problemForm, null, pageable, locale)
    }

    @PatchMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun update(@PathVariable("id") problemId: Long, @RequestBody p: Problem,
               @RequestParam(value = "locale", required = false) requestLocale: String?) {
        problemService.updateSelective(problemId, p, requestLocale)
    }

    companion object {
        private val log = LoggerFactory.getLogger(ProblemController::class.java)
    }
}
