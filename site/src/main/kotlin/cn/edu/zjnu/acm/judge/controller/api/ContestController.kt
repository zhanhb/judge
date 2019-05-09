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

import cn.edu.zjnu.acm.judge.data.form.ContestForm
import cn.edu.zjnu.acm.judge.domain.Contest
import cn.edu.zjnu.acm.judge.service.ContestService
import cn.edu.zjnu.acm.judge.service.impl.UserStanding
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.security.access.annotation.Secured
import org.springframework.web.bind.annotation.*
import java.io.IOException
import java.util.Locale
import java.util.concurrent.Future

/**
 *
 * @author zhanhb
 */
@RequestMapping(value = ["/api/contests"], produces = [APPLICATION_JSON_VALUE])
@RestController
@Secured("ROLE_ADMIN")
class ContestController(
        private val contestService: ContestService
) {

    @PostMapping
    fun save(@RequestBody contest: Contest): Contest {
        log.info("contest: {}", contest)
        return contestService.save(contest)
    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Throws(IOException::class)
    fun delete(@PathVariable("id") id: Long) {
        contestService.delete(id)
    }

    @GetMapping
    fun list(form: ContestForm): List<Contest> {
        log.info("form: {}", form)
        return contestService.findAll(form)
    }

    @GetMapping("{id}")
    fun findOne(@PathVariable("id") contestId: Long, locale: Locale): Contest {
        return contestService.getContestAndProblems(contestId, locale)
    }

    @PatchMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun update(@PathVariable("id") id: Long, @RequestBody contest: Contest) {
        contestService.updateSelective(id, contest)
    }

    @GetMapping("{id}/standing")
    fun standing(@PathVariable("id") id: Long): Future<List<UserStanding>> {
        return contestService.standingAsync(id)
    }

    companion object {
        private val log = LoggerFactory.getLogger(ContestController::class.java)
    }
}
