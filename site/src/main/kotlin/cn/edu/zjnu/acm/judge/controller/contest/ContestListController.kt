package cn.edu.zjnu.acm.judge.controller.contest

import cn.edu.zjnu.acm.judge.data.form.ContestStatus
import cn.edu.zjnu.acm.judge.exception.BusinessCode
import cn.edu.zjnu.acm.judge.exception.BusinessException
import cn.edu.zjnu.acm.judge.service.ContestService
import org.springframework.http.MediaType.TEXT_HTML_VALUE
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.servlet.mvc.support.RedirectAttributes

/**
 * @author zhanhb
 */
@Controller
@RequestMapping(produces = [TEXT_HTML_VALUE])
class ContestListController(
        private val contestService: ContestService
) {

    @GetMapping("contests")
    fun contests(model: Model, redirectAttributes: RedirectAttributes): String {
        return execute(model, "Contests", BusinessCode.NO_CONTESTS, redirectAttributes, ContestStatus.PENDING, ContestStatus.RUNNING, ContestStatus.ENDED)
    }

    @GetMapping("scheduledcontests")
    fun scheduledContests(model: Model, redirectAttributes: RedirectAttributes): String {
        return execute(model, "Scheduled Contests", BusinessCode.NO_SCHEDULED_CONTESTS, redirectAttributes, ContestStatus.PENDING)
    }

    @GetMapping("pastcontests")
    fun pastContests(model: Model, redirectAttributes: RedirectAttributes): String {
        return execute(model, "Contests", BusinessCode.NO_PAST_CONTESTS, redirectAttributes, ContestStatus.ENDED)
    }

    @GetMapping("currentcontests")
    fun currentContests(model: Model, redirectAttributes: RedirectAttributes): String {
        return execute(model, "Current Contests", BusinessCode.NO_CURRENT_CONTESTS, redirectAttributes, ContestStatus.RUNNING)
    }

    private fun execute(model: Model, title: String, businessCode: BusinessCode, redirectAttributes: RedirectAttributes,
                        status: ContestStatus, vararg rest: ContestStatus): String {
        val contests = contestService.findAll(status, *rest)
        if (contests.isEmpty()) {
            throw BusinessException(businessCode)
        } else if (contests.size == 1) {
            redirectAttributes.addAttribute("contest_id", contests[0].id)
            return "redirect:/showcontest"
        } else {
            model.addAttribute("title", title)
            model.addAttribute("contests", contests)
            return "contests/index"
        }
    }

}
