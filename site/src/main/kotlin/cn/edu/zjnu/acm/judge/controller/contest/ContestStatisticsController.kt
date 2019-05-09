package cn.edu.zjnu.acm.judge.controller.contest

import cn.edu.zjnu.acm.judge.service.ContestService
import cn.edu.zjnu.acm.judge.service.LanguageService
import cn.edu.zjnu.acm.judge.util.JudgeUtils
import org.slf4j.LoggerFactory
import org.springframework.http.MediaType.TEXT_HTML_VALUE
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.unbescape.html.HtmlEscape
import java.sql.SQLException
import java.time.Instant
import javax.servlet.http.HttpServletRequest
import javax.sql.DataSource

@Controller
@RequestMapping(produces = [TEXT_HTML_VALUE])
class ContestStatisticsController(
        private val dataSource: DataSource,
        private val contestService: ContestService,
        private val languageService: LanguageService
) {

    @GetMapping("conteststatistics")
    @Throws(SQLException::class)
    fun contestStatistics(
            request: HttpServletRequest,
            model: Model,
            @RequestParam("contest_id") contestId: Long): String {
        val now = Instant.now()
        val contest = contestService.findOneByIdAndNotDisabled(contestId)
        val title = contest.title
        val endTime = contest.endTime
        model.addAttribute("contestId", contestId)

        model.addAttribute("title", "Contest Statistics")
        val sb = StringBuilder("<p align=center><font size=5 color=blue>Contest Statistics--")
        sb.append(HtmlEscape.escapeHtml4Xml(title))
        if (!contest.isEnded) {
            if (endTime != null) {
                sb.append("<br/>Time to go:").append(JudgeUtils.formatTime(now, endTime))
            } else {
                sb.append("<br/>Time to go Infinity")
            }
        }
        sb.append("</font></p>"
                + "<TABLE align=center cellSpacing=0 cellPadding=0 width=600 border=1 class=table-back style=\"border-collapse: collapse\" bordercolor=#FFF>"
                + "<tr bgcolor=#6589D1><th>&nbsp;</th><th>100</th><th>99~70</th><th>69~31</th><th>30~1</th><th>0</th><th>CE</th><th>Others</th><th>Total</th>")

        val languages = languageService.availableLanguages
        val languageCount = languages.size
        val sql = StringBuilder(600).append("select ")
        for (i in languages.keys) {
            sql.append("sum(if(language=").append(i).append(",1,0)) g").append(i).append(",")
        }
        sql.append("s.problem_id,sum(if(score=100,1,0)) A,sum(if(score<100 and score >=70,1,0)) B,sum(if(score<70 and score >30,1,0)) D,sum(if(score>0 and score <=30,1,0)) C,sum(if(score=0,1,0)) E,sum(if(score=-7,1,0)) F,sum(if(score<-7 or score > 100,1,0)) G,count(*) Total from contest_problem cp left join solution s on cp.problem_id=s.problem_id and cp.contest_id=s.contest_id where s.contest_id=? group by cp.problem_id order by cp.num")

        val judgeStatus = arrayOf("A", "B", "C", "D", "E", "F", "G", "Total")
        val byScore = LongArray(judgeStatus.size)
        val byLanguage = LongArray(languageCount)
        sb.append("<th>&nbsp;</th>")
        languages.values
                .map({ HtmlEscape.escapeHtml4Xml(it.name) })
                .forEach { languageName -> sb.append("<th>").append(languageName).append("</th>") }
        sb.append("</tr>")
        log.debug("{}", sql)

        val problemsMap = contestService.getProblemsMap(contestId)
        dataSource.connection.use { conn ->
            conn.prepareStatement(sql.toString()).use { ps ->
                ps.setLong(1, contestId)
                ps.executeQuery().use { rs ->
                    while (rs.next()) {
                        val problemId = rs.getLong("problem_id")
                        val num = problemsMap[problemId]
                        if (num != null) {
                            sb.append("<tr><th><a href=contests/").append(contestId)
                                    .append("/problems/").append(num[1]).append(".html>")
                                    .append(contestService.toProblemIndex(num[0])).append("</a></th>")
                        }
                        for (i in judgeStatus.indices) {
                            val value = rs.getLong(judgeStatus[i])
                            byScore[i] += value
                            if (value == 0L) {
                                sb.append("<td>&nbsp;</td>")
                            } else if (i == judgeStatus.size - 1) {
                                sb.append("<th><a href=status?contest_id=").append(contestId).append("&problem_id=").append(problemId).append(">").append(value).append("</a></th>")
                            } else {
                                sb.append("<td>").append(value).append("</td>")
                            }
                        }
                        sb.append("<td>&nbsp;</td>")
                        for (i in 0 until languageCount) {
                            val value = rs.getLong(i + 1)
                            byLanguage[i] += value
                            if (value == 0L) {
                                sb.append("<td>&nbsp;</td>")
                            } else {
                                sb.append("<td>").append(value).append("</td>")
                            }
                        }
                        sb.append("</tr>")
                    }
                }
            }
        }
        sb.append("<tr><th>Total</th>")
        for (i in judgeStatus.indices) {
            if (byScore[i] == 0L) {
                sb.append("<td>&nbsp;</td>")
            } else if (i == judgeStatus.size - 1) {
                sb.append("<th>").append(byScore[i]).append("</th>")
            } else {
                sb.append("<td>").append(byScore[i]).append("</td>")
            }
        }
        sb.append("<td>&nbsp;</td>")
        for (i in 0 until languageCount) {
            if (byLanguage[i] == 0L) {
                sb.append("<td>&nbsp;</td>")
            } else {
                sb.append("<td>").append(byLanguage[i]).append("</td>")
            }
        }
        sb.append("</tr></table>")
        model.addAttribute("content", sb.toString())
        return "legacy"
    }

    companion object {
        private val log = LoggerFactory.getLogger(ContestStatisticsController::class.java)
    }

}
