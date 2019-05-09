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
package cn.edu.zjnu.acm.judge.config

import cn.edu.zjnu.acm.judge.mapper.MailMapper
import org.slf4j.LoggerFactory
import org.springframework.security.core.Authentication
import org.springframework.util.StringUtils
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.RequestParam
import javax.servlet.RequestDispatcher
import javax.servlet.http.HttpServletRequest

/**
 *
 * @author zhanhb
 */
@ControllerAdvice
class JudgeHandlerInterceptor(
        @Suppress("SpringJavaInjectionPointsAutowiringInspection") private val mailMapper: MailMapper
) {
    @ModelAttribute
    fun addAttributes(request: HttpServletRequest,
                      @RequestParam(value = "url", required = false) url: String?,
                      authentication: Authentication?) {
        log.trace("entering attributes, url={}, auth={}", url, authentication)
        if (java.lang.Boolean.TRUE == request.getAttribute(APPLIED_ONCE_KEY)) {
            return
        }
        request.setAttribute(APPLIED_ONCE_KEY, true)

        if (StringUtils.hasText(url)) {
            request.setAttribute(BACK_URL_ATTRIBUTE_NAME, url)
        } else {
            var uri = getString(RequestDispatcher.FORWARD_SERVLET_PATH, { it.servletPath }, request)
            val query = getString(RequestDispatcher.FORWARD_QUERY_STRING, { it.queryString }, request)
            if (query != null) {
                uri = "$uri?$query"
            }
            request.setAttribute(BACK_URL_ATTRIBUTE_NAME, uri)
        }
        log.debug("authentication: {}", authentication)
        val name = authentication?.name
        if (name != null) {
            val mailInfo = mailMapper.getMailInfo(name)
            if (mailInfo != null) {
                request.setAttribute("mailInfo", mailInfo)
            }
        }
    }

    companion object {

        private val log = LoggerFactory.getLogger(JudgeHandlerInterceptor::class.java)

        private val APPLIED_ONCE_KEY = JudgeHandlerInterceptor::class.java.name + ".APPLIED_ONCE"
        val BACK_URL_ATTRIBUTE_NAME = "backUrl"

        private fun getString(attributeName: String, supplier: (HttpServletRequest) -> String?, request: HttpServletRequest): String? {
            val attribute = request.getAttribute(attributeName) as String?
            return if (attribute != null) attribute else supplier(request)
        }
    }

}
