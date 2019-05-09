package cn.edu.zjnu.acm.judge.controller.submission

import cn.edu.zjnu.acm.judge.service.RejudgeService
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.http.ResponseEntity
import org.springframework.security.access.annotation.Secured
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.concurrent.CompletableFuture

/**
 * @author zhanhb
 */
@RequestMapping(produces = [APPLICATION_JSON_VALUE])
@RestController
@Secured("ROLE_ADMIN")
class RejudgeController(
        private val rejudgeService: RejudgeService
) {

    // TODO request method
    @GetMapping(value = ["admin/rejudge"], params = ["solution_id"])
    fun rejudgeSolution(
            @RequestParam("solution_id") submissionId: Long): CompletableFuture<*> {
        return rejudgeService.bySubmissionId(submissionId)
    }

    @GetMapping(value = ["admin/rejudge"], params = ["problem_id"])
    fun rejudgeProblem(@RequestParam("problem_id") problemId: Long): ResponseEntity<*> {
        rejudgeService.byProblemId(problemId)
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(mapOf("message" to "重新评测请求已经受理"))
    }

}
