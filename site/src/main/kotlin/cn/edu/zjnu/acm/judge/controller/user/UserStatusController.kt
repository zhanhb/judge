package cn.edu.zjnu.acm.judge.controller.user

import cn.edu.zjnu.acm.judge.mapper.UserMapper
import cn.edu.zjnu.acm.judge.mapper.UserProblemMapper
import cn.edu.zjnu.acm.judge.service.AccountService
import org.slf4j.LoggerFactory
import org.springframework.http.MediaType
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

/**
 * @author zhanhb
 */
@Controller
@RequestMapping(produces = [MediaType.TEXT_HTML_VALUE])
class UserStatusController(
        @Suppress("SpringJavaInjectionPointsAutowiringInspection") private val userMapper: UserMapper,
        @Suppress("SpringJavaInjectionPointsAutowiringInspection") private val userProblemMapper: UserProblemMapper,
        private val accountService: AccountService
) {
    @GetMapping("userstatus")
    fun userStatus(model: Model,
                   @RequestParam(value = "size", defaultValue = "3") display: Int,
                   @RequestParam(value = "user_id", required = false) userId: String?): String {
        var display = display
        var userId = userId
        val user = accountService.findOne(userId!!)
        display = Math.max(Math.min(display, 9), 1)
        userId = user.id
        val rank = userMapper.rank(userId!!) + 1
        val rankFirst = Math.max(rank - display, 1)
        val neighbours = userMapper.neighbours(userId, display)
        val solvedProblems = userProblemMapper.findAllSolvedByUserId(userId)
        model.addAttribute("neighbours", neighbours)
        model.addAttribute("solvedProblems", solvedProblems)
        model.addAttribute("rankFirst", rankFirst)
        model.addAttribute("user", user)
        model.addAttribute("rank", rank)

        log.debug("rankFirst = {}", rankFirst)
        return "users/status"
    }

    companion object {
        private val log = LoggerFactory.getLogger(UserStatusController::class.java)
    }
}
