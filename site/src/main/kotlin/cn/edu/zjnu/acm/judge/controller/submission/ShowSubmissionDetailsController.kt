package cn.edu.zjnu.acm.judge.controller.submission

import cn.edu.zjnu.acm.judge.exception.BusinessCode
import cn.edu.zjnu.acm.judge.exception.BusinessException
import cn.edu.zjnu.acm.judge.mapper.ContestMapper
import cn.edu.zjnu.acm.judge.mapper.SubmissionMapper
import cn.edu.zjnu.acm.judge.service.SubmissionService
import cn.edu.zjnu.acm.judge.util.ResultType
import org.springframework.http.MediaType
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import javax.servlet.http.HttpServletRequest

/**
 * @author zhanhb
 */
@Controller
@RequestMapping(produces = [MediaType.TEXT_HTML_VALUE])
class ShowSubmissionDetailsController(
        @Suppress("SpringJavaInjectionPointsAutowiringInspection") private val submissionMapper: SubmissionMapper,
        @Suppress("SpringJavaInjectionPointsAutowiringInspection") private val contestMapper: ContestMapper,
        private val submissionService: SubmissionService
) {

    @GetMapping("showsolutiondetails")
    fun showSolutionDetails(
            request: HttpServletRequest,
            @RequestParam("solution_id") submissionId: Long): String {
        val submission = submissionMapper.findOne(submissionId)
                ?: throw BusinessException(BusinessCode.SUBMISSION_NOT_FOUND, submissionId)
        val contestId = submission.contest
        if (contestId != null) {
            val contest = contestMapper.findOne(contestId)
            if (contest != null) {
                request.setAttribute("contestId", contest.id)
            }
        }
        if (!submissionService.canView(request, submission)) {
            throw BusinessException(BusinessCode.VIEW_SOURCE_PERMISSION_DENIED, submissionId)
        }
        val details = submissionService.getSubmissionDetail(submissionId)
        request.setAttribute("details", details)
        request.setAttribute("user", submission.user)
        request.setAttribute("problem", submission.problem)
        request.setAttribute("result", ResultType.getResultDescription(submission.score))
        request.setAttribute("score", submission.score)
        request.setAttribute("time", submission.time)
        request.setAttribute("memory", submission.memory)
        return "submissions/detail"
    }

}
