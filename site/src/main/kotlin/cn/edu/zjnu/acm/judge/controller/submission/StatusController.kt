package cn.edu.zjnu.acm.judge.controller.submission

import cn.edu.zjnu.acm.judge.data.form.SubmissionQueryForm
import cn.edu.zjnu.acm.judge.mapper.ContestMapper
import cn.edu.zjnu.acm.judge.mapper.SubmissionMapper
import cn.edu.zjnu.acm.judge.service.ContestService
import cn.edu.zjnu.acm.judge.service.LanguageService
import cn.edu.zjnu.acm.judge.service.SubmissionService
import cn.edu.zjnu.acm.judge.service.impl.UserDetailsServiceImpl
import cn.edu.zjnu.acm.judge.util.ResultType
import cn.edu.zjnu.acm.judge.util.URIBuilder
import org.slf4j.LoggerFactory
import org.springframework.http.MediaType.TEXT_HTML_VALUE
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.unbescape.html.HtmlEscape
import java.text.DecimalFormat
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import javax.servlet.http.HttpServletRequest

/**
 * @author zhanhb
 */
@Controller
@RequestMapping(produces = [TEXT_HTML_VALUE])
class StatusController(
        @Suppress("SpringJavaInjectionPointsAutowiringInspection") private val submissionMapper: SubmissionMapper,
        @Suppress("SpringJavaInjectionPointsAutowiringInspection") private val contestMapper: ContestMapper,
        private val contestService: ContestService,
        private val submissionService: SubmissionService,
        private val languageService: LanguageService
) {

    @GetMapping("status", "submissions")
    fun status(request: HttpServletRequest,
               @RequestParam(value = "problem_id", defaultValue = "") pid: String,
               @RequestParam(value = "contest_id", required = false) contestId: Long?,
               @RequestParam(value = "language", defaultValue = "-1") language: Int,
               @RequestParam(value = "size", defaultValue = "20") size: Int,
               @RequestParam(value = "bottom", required = false) bottom: Long?,
               @RequestParam(value = "score", required = false) sc: Int?,
               @RequestParam(value = "user_id", defaultValue = "") userId: String,
               @RequestParam(value = "top", required = false) top: Long?,
               authentication: Authentication?,
               model: Model): String {
        var problemId: Long = 0
        var query = URIBuilder.fromRequest(request)
                .replaceQueryParam("top")
                .replaceQueryParam("bottom")
                .toString()
        log.debug("query={}", query)
        var problemsMap = emptyMap<Long, LongArray>()
        if (contestId != null) {
            problemsMap = contestService.getProblemsMap(contestId)
        }
        try {
            problemId = java.lang.Long.parseLong(pid)
        } catch (ex: NumberFormatException) {
            if (contestId != null && pid.length == 1) {
                // TODO the character is the index in the list.
                val x = Character.toUpperCase(pid[0]) - 'A'
                for ((key, value) in problemsMap) {
                    if (x.toLong() == value[0]) {
                        problemId = key
                        break
                    }
                }
            }
        }

        val form = SubmissionQueryForm(
                problem = if (problemId == 0L) null else problemId,
                contest = contestId,
                score = sc,
                size = Math.max(Math.min(size, 500), 0),
                top = top,
                bottom = bottom,
                user = if (userId.isBlank()) null else userId.trim(),
                language = if (language != -1) language else null
        )
        log.debug("{}", form)
        val submissions = submissionMapper.findAllByCriteria(form)

        val min = submissions.map { it.id }.min()
        val max = submissions.map { it.id }.max()

        model.addAttribute("contestId", contestId)
        model.addAttribute("title", "Problem Status List")

        val sb = StringBuilder(
                "<p align=center><font size=4 color=#339>Problem Status List</font></p>" + "<form method=get action='status'/><label for='pid'>Problem ID:</label><input id='pid' type=text name=problem_id size=8 value=\"")
                .append(HtmlEscape.escapeHtml4Xml(pid)).append("\"/> <label for='uid'>User ID:</label><input id='uid' type=text name=user_id size=15 value=\"")
                .append(HtmlEscape.escapeHtml4Xml(userId)).append("\"/>"
                        + " <label for='languag'>Language:</label>"
                        + "<select id='languag' size=\"1\" name=\"language\">"
                        + "<option value=\"\">All</option>")
        for ((key, value) in languageService.availableLanguages) {
            sb.append("<option value=\"").append(key).append("\"").append(if (key == language) " selected" else "").append(">").append(HtmlEscape.escapeHtml4Xml(value.name)).append("</option>")
        }
        sb.append("</select>")
        if (contestId != null) {
            sb.append("<input type=hidden name=contest_id value='").append(contestId).append("' />")
        }
        sb.append(" <button type=submit>Go</button></form>"
                + "<TABLE cellSpacing=0 cellPadding=0 width=100% border=1 class=table-back style=\"border-collapse: collapse\" bordercolor=#FFF>"
                + "<tr bgcolor=#6589D1><td align=center width=8%><b>Run ID</b></td><td align=center width=10%><b>User</b></td><td align=center width=6%><b>Problem</b></td>"
                + "<td align=center width=10%><b>Result</b></td><td align=center width=10%><b>Score</b></td><td align=center width=7%><b>Memory</b></td><td align=center width=7%><b>Time</b></td><td align=center width=7%><b>Language</b></td><td align=center width=7%><b>Code Length</b></td><td align=center width=17%><b>Submit Time</b></td></tr>")
        val admin = UserDetailsServiceImpl.isAdminLoginned(request)
        val sourceBrowser = UserDetailsServiceImpl.isSourceBrowser(request)

        for (submission in submissions) {
            val id = submission.id
            val user_id1 = submission.user
            val problem_id1 = submission.problem
            val contest_id1 = submission.contest
            val num = problemsMap.getOrDefault(problem_id1, longArrayOf(-1))
            val score = submission.score
            val inDate = submission.inDate
            val language1 = languageService.getLanguageName(submission.language)
            val color: String
            if (score == 100) {
                color = "blue"
            } else {
                color = "red"
            }
            sb.append("<tr align=center><td>").append(id).append("</td>")
            sb.append("<td><a href=userstatus?user_id=").append(user_id1)
                    .append(">").append(user_id1).append("</a></td>")
            if (contestId == null || num[0] == -1L) {
                sb.append("<td><a href=showproblem?problem_id=")
                        .append(problem_id1).append(">").append(problem_id1).append("</a></td>")
            } else {
                sb.append("<td><a href=contests/")
                        .append(contestId).append("/problems/").append(num[1])
                        .append(".html>").append(contestService.toProblemIndex(num[0])).append("</a></td>")
            }
            if (score == ResultType.COMPILE_ERROR) {
                if (submissionService.canView(request, submission)) {
                    sb.append("<td><a href=\"showcompileinfo?solution_id=").append(id).append("\" target=_blank><font color=green>").append(ResultType.getResultDescription(score)).append("</font></a></td>")
                } else {
                    sb.append("<td><font color=green>").append(ResultType.getResultDescription(score)).append("</font></td>")
                }
            } else if (submissionService.canView(request, submission)) {
                sb.append("<td><a href=showsolutiondetails?solution_id=").append(id).append(" target=_blank><strong><font color=").append(color).append(">").append(ResultType.getResultDescription(score)).append("</font></strong></a></td>")
            } else {
                sb.append("<td><font color=").append(color).append(">").append(ResultType.getResultDescription(score)).append("</font></a></td>")
            }
            if (score <= 100 && score >= 0) {
                sb.append("<td>").append(score).append("</td>")
            } else {
                sb.append("<td>&nbsp;</td>")
            }
            var ended = true
            if (!admin && contest_id1 != null) {
                ended = contestMapper.findOne(contest_id1)!!.isEnded
            }
            if (score == 100 && ended) {
                sb.append("<td>").append(submission.memory).append("K</td><td>").append(submission.time).append("MS</td>")
            } else {
                sb.append("<td>&nbsp;</td><td>&nbsp;</td>")
            }
            if (admin || sourceBrowser || UserDetailsServiceImpl.isUser(authentication, user_id1)) {
                sb.append("<td><a href=showsource?solution_id=").append(id).append(" target=_blank>").append(language1).append("</a></td>")
            } else {
                sb.append("<td>").append(language1).append("</td>")
            }
            if (ended) {
                val sourceLength = submission.sourceLength
                val t = if (sourceLength > 2048) DecimalFormat("0.00").format(sourceLength / 1024.0) + " KB" else "$sourceLength B"
                sb.append("<td>").append(t).append("</td>")
            } else {
                sb.append("<td>&nbsp;</td>")
            }
            sb.append("<td>").append(dtf.format(inDate!!.atZone(ZoneId.systemDefault()).toLocalDateTime())).append("</td></tr>")
        }
        query = request.contextPath + query
        sb.append("</table><p align=center>[<a href=\"").append(query).append("\">Top</a>]&nbsp;&nbsp;")
        query += if (query.contains("?")) '&' else '?'
        sb.append("[<a href=\"").append(query)
                .append("bottom=")
                .append(max?.toString() ?: "")
                .append("\"><font color=blue>Previous Page</font></a>]" + "&nbsp;&nbsp;[<a href=\"")
                .append(query).append("top=")
                .append(min?.toString() ?: "")
                .append("\"><font color=blue>Next Page</font></a>]&nbsp;&nbsp;</p>" + "<script>!function(w){setTimeout(function(){w.location.reload()},60000)}(this)</script>")
        model.addAttribute("content", sb.toString())
        return "legacy"
    }

    companion object {
        private val log = LoggerFactory.getLogger(StatusController::class.java)
        private val dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    }

}
