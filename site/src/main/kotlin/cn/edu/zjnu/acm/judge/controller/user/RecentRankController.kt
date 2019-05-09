package cn.edu.zjnu.acm.judge.controller.user

import cn.edu.zjnu.acm.judge.mapper.UserMapper
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
class RecentRankController(
        @Suppress("SpringJavaInjectionPointsAutowiringInspection") private val userMapper: UserMapper
) {
    @GetMapping("recentrank")
    fun recentRank(model: Model,
                   @RequestParam(value = "count", defaultValue = "10000") count: Int): String {
        var count = count
        count = Math.max(0, Math.min(10000, count))
        val recentRank = userMapper.recentrank(count)
        model.addAttribute("count", count)
        model.addAttribute("recentrank", recentRank)
        return "users/recent"
    }

}
