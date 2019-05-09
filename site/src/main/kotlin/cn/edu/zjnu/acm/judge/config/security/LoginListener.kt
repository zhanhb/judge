/*
 * Copyright 2017-2019 ZJNU ACM.
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

import cn.edu.zjnu.acm.judge.domain.LoginLog
import cn.edu.zjnu.acm.judge.mapper.UserMapper
import cn.edu.zjnu.acm.judge.service.LoginlogService
import org.springframework.context.ApplicationListener
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent
import org.springframework.security.authentication.event.InteractiveAuthenticationSuccessEvent
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.WebAuthenticationDetails
import java.time.Instant

/**
 *
 * @author zhanhb
 */
@Configuration
class LoginListener {
    companion object {

        private fun saveEvent(loginlogService: LoginlogService, authentication: Authentication): String? {
            val details = authentication.details
            var remoteAddress: String? = ""
            val success = authentication.isAuthenticated
            var type = authentication.javaClass.simpleName
            val tokenSuffix = "AuthenticationToken"
            if (type.endsWith(tokenSuffix)) {
                type = type.substring(0, type.length - tokenSuffix.length)
            }
            if (details is WebAuthenticationDetails) remoteAddress = details.remoteAddress
            loginlogService.save(LoginLog(ip = remoteAddress, success = success, type = type, user = authentication.name))
            return remoteAddress
        }

    }

    @Configuration
    class LoginSuccessListener(
            private val loginlogService: LoginlogService,
            @Suppress("SpringJavaInjectionPointsAutowiringInspection") private val userMapper: UserMapper) :
            ApplicationListener<InteractiveAuthenticationSuccessEvent> {

        override fun onApplicationEvent(event: InteractiveAuthenticationSuccessEvent) {
            val authentication = event.authentication
            val ip = saveEvent(loginlogService, authentication)

            val user = userMapper.findOne(authentication.name)

            if (user != null) {
                userMapper.updateSelective(user.id!!, user.copy(accesstime = Instant.now(), ip = ip))
            }
        }

    }

    @Configuration
    class LoginFailureListener(private val loginlogService: LoginlogService) : ApplicationListener<AuthenticationFailureBadCredentialsEvent> {

        override fun onApplicationEvent(event: AuthenticationFailureBadCredentialsEvent) {
            val authentication = event.authentication
            saveEvent(loginlogService, authentication)
        }

    }

}
