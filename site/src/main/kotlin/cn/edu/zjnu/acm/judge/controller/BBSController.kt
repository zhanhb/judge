package cn.edu.zjnu.acm.judge.controller

import cn.edu.zjnu.acm.judge.exception.BusinessCode
import cn.edu.zjnu.acm.judge.exception.BusinessException
import cn.edu.zjnu.acm.judge.mapper.MessageMapper
import cn.edu.zjnu.acm.judge.service.MessageService
import cn.edu.zjnu.acm.judge.util.JudgeUtils
import cn.edu.zjnu.acm.judge.util.URIBuilder
import org.springframework.http.MediaType.TEXT_HTML_VALUE
import org.springframework.security.access.annotation.Secured
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Controller
import org.springframework.transaction.annotation.Transactional
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.servlet.mvc.support.RedirectAttributes
import org.unbescape.html.HtmlEscape
import java.sql.Timestamp
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import javax.servlet.http.HttpServletRequest

/**
 * @author zhanhb
 */
@Controller
@RequestMapping(produces = [TEXT_HTML_VALUE])
class BBSController(
        @Suppress("SpringJavaInjectionPointsAutowiringInspection") private val messageMapper: MessageMapper,
        private val messageService: MessageService
) {

    @GetMapping("bbs")
    fun bbs(request: HttpServletRequest,
            @RequestParam(value = "problem_id", required = false) problemId: Long?,
            @RequestParam(value = "size", defaultValue = "50") threadLimit: Int,
            @RequestParam(value = "top", defaultValue = "99999999") top: Long,
            model: Model): String {
        val limit = Math.max(Math.min(threadLimit, 500), 0)

        val mint = messageMapper.mint(top, problemId, limit, 0)
        val messages = messageMapper.findAllByThreadIdBetween(mint, top, problemId, null)

        var currentDepth: Long = 0
        var lastThreadId: Long = 0
        model.addAttribute("title", "Messages")

        val sb = StringBuilder("<table width=100% class=\"table-default table-back\">" + "<tr><td><ul>")
        val maxThreadId = messages.map { it.thread }.max() ?: 0
        val maxt = messageMapper.maxt(maxThreadId, problemId, limit, 999999999999L)
        for ((messageId, inDate, _, userId, _, title, problem, depth, threadId) in messages) {
            val timestamp = Timestamp.from(inDate!!)

            while (currentDepth < depth) {
                sb.append("<ul>")
                currentDepth++
            }
            while (currentDepth > depth) {
                sb.append("</ul>")
                currentDepth--
            }
            if (lastThreadId != 0L && threadId != lastThreadId && depth == 0L) {
                sb.append("<hr/>")
            }
            lastThreadId = threadId
            sb.append("<li><a href=\"showmessage?message_id=").append(messageId)
                    .append("\"><font color=\"blue\">")
                    .append(HtmlEscape.escapeHtml4Xml(title))
                    .append("</font></a> <b><a href=\"userstatus?user_id=")
                    .append(userId).append("\"><font color=\"black\">")
                    .append(userId).append("</font></a></b> ")
                    .append(timestamp)
            if (problem != null && problem != 0L && depth == 0L) {
                sb.append(" <b><a href=\"showproblem?problem_id=").append(problem)
                        .append("\"><font color=\"black\">Problem ").append(problem)
                        .append("</font></a></b>")
            }
        }
        while (currentDepth > 0) {
            sb.append("</ul>")
            currentDepth--
        }
        sb.append("</ul></td></tr></table><center>")
        val query = URIBuilder.fromRequest(request)
                .replacePath("bbs")
                .replaceQueryParam("top")
        sb.append("<hr/>[<a href=\"").append(query).append("\">Top</a>]")
        query.replaceQueryParam("top", java.lang.Long.toString(maxt))
        sb.append("&nbsp;&nbsp;&nbsp;[<a href=\"").append(query).append("\">Previous</a>]")
        query.replaceQueryParam("top", java.lang.Long.toString(mint))
        sb.append("&nbsp;&nbsp;&nbsp;[<a href=\"").append(query)
                .append("\">Next</a>]<br/></center><form action=\"postpage\">")
        if (problemId != null) {
            sb.append("<input type=\"hidden\" name=\"problem_id\" value=\"").append(problemId).append("\">")
        }
        sb.append("<button type=\"submit\">Post new message</button></form>")
        model.addAttribute("content", sb.toString())
        return "legacy"
    }

    @Secured("ROLE_USER")
    @GetMapping("/postpage", "/post")
    fun postpage(model: Model,
                 @RequestParam(value = "problem_id", required = false) problemId: Long?): String {
        model.addAttribute("problemId", problemId)
        return "bbs/postpage"
    }

    @PostMapping("post")
    @Secured("ROLE_USER")
    @Transactional
    fun post(@RequestParam(value = "problem_id", required = false) problemId: Long?,
             @RequestParam(value = "parent_id", required = false) parentId: Long?,
             @RequestParam(value = "content", defaultValue = "") content: String,
             @RequestParam(value = "title", defaultValue = "") title: String,
             redirectAttributes: RedirectAttributes,
             authentication: Authentication?): String {
        val userId = authentication?.name
        if (title.isBlank()) {
            throw BusinessException(BusinessCode.MESSAGE_EMPTY_TITLE)
        }

        messageService.save(parentId, problemId, userId!!, title, content)

        if (problemId != null) {
            redirectAttributes.addAttribute("problem_id", problemId)
        }
        return "redirect:/bbs"
    }

    @Secured("ROLE_USER")
    @GetMapping("showmessage")
    fun showMessage(
            @RequestParam("message_id") messageId: Long,
            model: Model): String {
        val message = messageMapper.findOne(messageId)
                ?: throw BusinessException(BusinessCode.MESSAGE_NOT_FOUND)
        val problemId = message.problem
        val parentId=message.parent
        val title = message.title
        val content = message.content
        val formatter = dtf.withZone(ZoneId.systemDefault())
        val depth = message.depth + 1

        model.addAttribute("title", "Detail of message")
        val sb = StringBuilder("<table border=\"0\" width=\"980\" class=\"table-back\">"
                + "<tr><td>" + "<center><h2><font color=\"blue\">")
                .append(HtmlEscape.escapeHtml4Xml(message.title))
                .append("</font></h2></center>" + "Posted by <b><a href=\"userstatus?user_id=")
                .append(message.user).append("\"><font color=\"black\">")
                .append(message.user).append("</font></a></b>" + "at ")
                .append(formatter.format(message.inDate!!))
        if (problemId != null && problemId != 0L) {
            sb.append("on <b><a href=\"showproblem?problem_id=").append(problemId)
                    .append("\"><font color=\"black\">Problem ").append(problemId)
                    .append("</font></a></b>")
        }
        if (parentId != null && parentId != 0L) {
            val parent = messageMapper.findOne(parentId)
            if (parent != null) {
                val title1 = parent.title
                val inDate1 = parent.inDate
                sb.append("<br/>In Reply To:<a href=\"showmessage?message_id=")
                        .append(parentId).append("\"><font color=\"blue\">")
                        .append(HtmlEscape.escapeHtml4Xml(title1))
                        .append("</font></a>" + "Posted by:<b><a href=\"userstatus?user_id=")
                        .append(parent.user).append("\"><font color=\"black\">")
                        .append(parent.user).append("</font></a></b>" + "at ")
                        .append(formatter.format(inDate1!!))
            }
        }
        sb.append("<HR noshade color=#FFF><pre>")
        sb.append(HtmlEscape.escapeHtml4Xml(message.content))
        sb.append("</pre><HR noshade color=\"#FFF\"><b>Followed by:</b><br/><ul>")
        var dep = depth
        val messages = messageMapper.findAllByThreadIdAndOrderNumGreaterThanOrderByOrderNum(message.thread, message.order)
        for ((id, inDate1, _, user, _, title1, _, depth1) in messages) {
            if (depth1 < depth) {
                break
            }
            for (i in dep until depth1) {
                sb.append("<ul>")
            }
            for (i in depth1 until dep) {
                sb.append("</ul>")
            }
            sb.append("<li><a href=\"showmessage?message_id=")
                    .append(id).append("\"><font color=\"blue\">")
                    .append(HtmlEscape.escapeHtml4Xml(title1))
                    .append("</font></a>" + " -- <b><a href=\"userstatus?user_id=")
                    .append(user)
                    .append("\"><font color=\"black\">")
                    .append(user)
                    .append("</font></a></b> ")
            sb.append(formatter.format(inDate1!!))
            dep = depth1
        }
        for (i in depth until dep) {
            sb.append("</ul>")
        }
        sb.append("</ul>" + "<HR noshade color=\"#FFF\">" + "<font color=\"blue\">Post your reply here:</font><br/>" + "<form method=\"POST\" action=\"post\">")
        if (problemId != null) {
            sb.append("<input type=\"hidden\" name=\"problem_id\" value=\"").append(problemId).append("\"/>")
        }
        sb.append("<input type=\"hidden\" name=\"parent_id\" value=\"").append(messageId).append("\"/>")
        sb.append("Title:<br/><input type=\"text\" name=\"title\" value=\"")
                .append(HtmlEscape.escapeHtml4Xml(if (!title!!.startsWith("re:", ignoreCase = true)) "Reply:$title" else title))
                .append("\" size=75><br/>" + "Content:<br/><textarea rows=\"15\" name=\"content\" cols=\"75\">")
                .append(JudgeUtils.getReplyString(content))
                .append("</textarea><br/><button type=\"submit\">reply</button></td></tr></table>")
        model.addAttribute("content", sb.toString())
        return "legacy"
    }

    companion object {

        private val dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    }

}
