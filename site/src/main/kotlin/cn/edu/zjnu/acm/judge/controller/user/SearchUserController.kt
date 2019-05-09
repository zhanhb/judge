package cn.edu.zjnu.acm.judge.controller.user

import cn.edu.zjnu.acm.judge.data.form.AccountForm
import cn.edu.zjnu.acm.judge.exception.BusinessCode
import cn.edu.zjnu.acm.judge.exception.BusinessException
import cn.edu.zjnu.acm.judge.service.AccountService
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.web.PageableDefault
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
class SearchUserController(
        private val accountService: AccountService
) {

    @GetMapping("searchuser")
    fun searchuser(model: Model,
                   @RequestParam(value = "user_id", defaultValue = "") keyword: String,
                   @RequestParam(value = "position", required = false) position: String?,
                   @PageableDefault(1000) pageable: Pageable): String {
        if (keyword.replace("%", "").length < 2) {
            throw BusinessException(BusinessCode.USER_SEARCH_KEYWORD_SHORT)
        }
        var like = keyword
        if (position == null) {
            like = "%$like%"
        } else if (position.equals("begin", ignoreCase = true)) {
            like += "%"
        } else {
            like = "%$like"
        }
        var t = pageable
        @Suppress("SENSELESS_COMPARISON")
        if (t.sort == null || t.sort.isEmpty) {
            t = PageRequest.of(pageable.pageNumber, pageable.pageSize, Sort.by(
                    Sort.Order.desc("solved"),
                    Sort.Order.asc("submit")
            ))
        }
        val users = accountService.findAll(AccountForm(disabled = false, query = like), t)
        model.addAttribute("query", keyword)
        model.addAttribute("users", users)
        return "users/search"
    }

}
