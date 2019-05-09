/*
 * Copyright 2016 ZJNU ACM.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.edu.zjnu.acm.judge.controller.user

import cn.edu.zjnu.acm.judge.service.AccountService
import cn.edu.zjnu.acm.judge.util.URIBuilder
import javax.servlet.http.HttpServletRequest
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.web.PageableDefault
import org.springframework.http.MediaType
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping

/**
 *
 * @author zhanhb
 */
@Controller
@RequestMapping(produces = [MediaType.TEXT_HTML_VALUE])
class UserListController(
        private val accountService: AccountService
) {

    @GetMapping("userlist", "users")
    fun userList(request: HttpServletRequest, @PageableDefault(50) pageable: Pageable): String {
        var pageable = pageable
        var sort: Sort? = pageable.sort
        val pageSize = Math.min(pageable.pageSize, 500)

        if (sort == null || !sort.iterator().hasNext()) {
            sort = DEFAULT_SORT
        }

        pageable = PageRequest.of(pageable.pageNumber, pageSize, sort)

        val query = URIBuilder.fromRequest(request)
                .replaceQueryParam("page")
                .toString()

        request.setAttribute("url", query)
        request.setAttribute("page", accountService.findAll(pageable))
        return "users/list"
    }

    companion object {

        private val DEFAULT_SORT = Sort.by(
                Sort.Order.desc("solved"),
                Sort.Order.asc("submit"),
                Sort.Order.asc("user_id")
        )
    }

}
