package cn.edu.zjnu.acm.judge.controller.submission

import cn.edu.zjnu.acm.judge.exception.BusinessCode
import cn.edu.zjnu.acm.judge.exception.BusinessException
import cn.edu.zjnu.acm.judge.mapper.SubmissionMapper
import cn.edu.zjnu.acm.judge.mapper.SubmissionDetailMapper
import cn.edu.zjnu.acm.judge.mapper.UserPreferenceMapper
import cn.edu.zjnu.acm.judge.service.ContestOnlyService
import cn.edu.zjnu.acm.judge.service.LanguageService
import cn.edu.zjnu.acm.judge.service.SubmissionService
import cn.edu.zjnu.acm.judge.util.ResultType
import org.springframework.http.MediaType.TEXT_HTML_VALUE
import org.springframework.security.access.annotation.Secured
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import javax.servlet.http.HttpServletRequest

/**
 * @author zhanhb
 */
@Controller
@RequestMapping(produces = [TEXT_HTML_VALUE])
class ShowSourceController(
        @Suppress("SpringJavaInjectionPointsAutowiringInspection") private val submissionMapper: SubmissionMapper,
        @Suppress("SpringJavaInjectionPointsAutowiringInspection") private val userPerferenceMapper: UserPreferenceMapper,
        private val submissionService: SubmissionService,
        private val submissionDetailMapper: SubmissionDetailMapper,
        private val languageService: LanguageService,
        private val contestOnlyService: ContestOnlyService
) {
    @Secured("ROLE_USER")
    @GetMapping("showsource")
    fun showSource(request: HttpServletRequest,
                   @RequestParam("solution_id") submissionId: Long,
                   @RequestParam(value = "style", required = false) style: Int?,
                   authentication: Authentication?): String {
        var style = style
        val submission = submissionMapper.findOne(submissionId)
                ?: throw BusinessException(BusinessCode.SUBMISSION_NOT_FOUND, submissionId)

        contestOnlyService.checkViewSource(request, submission)
        val userId = authentication?.name
        if (!submissionService.canView(request, submission)) {
            throw BusinessException(BusinessCode.VIEW_SOURCE_PERMISSION_DENIED, submissionId)
        }
        val language = languageService.getLanguageName(submission.language)

        if (style == null) {
            style = userPerferenceMapper.getStyle(userId!!)
        } else {
            userPerferenceMapper.setStyle(userId!!, style)
        }
        val source = submissionDetailMapper.findSourceById(submissionId)

        request.setAttribute("submission", submission)
        if (submission.contest != null) {
            request.setAttribute("contestId", submission.contest)
        }
        request.setAttribute("language", language)
        request.setAttribute("result", ResultType.getShowsourceString(submission.score))
        request.setAttribute("style", style)
        request.setAttribute("source", source)
        return "submissions/source"
    }

}
