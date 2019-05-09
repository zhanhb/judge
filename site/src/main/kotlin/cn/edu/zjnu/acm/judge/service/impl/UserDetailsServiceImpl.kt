/*
 * Copyright 2015 ZJNU ACM.
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
package cn.edu.zjnu.acm.judge.service.impl

import cn.edu.zjnu.acm.judge.mapper.UserMapper
import cn.edu.zjnu.acm.judge.mapper.UserRoleMapper
import com.google.common.collect.ImmutableList
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.AuthorityUtils
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import javax.servlet.http.HttpServletRequest

/**
 *
 * @author zhanhb
 */
@Service("userDetailsService")
class UserDetailsServiceImpl(
        @Suppress("SpringJavaInjectionPointsAutowiringInspection") private val userRoleMapper: UserRoleMapper,
        @Suppress("SpringJavaInjectionPointsAutowiringInspection") private val userMapper: UserMapper
) : UserDetailsService {

    @Throws(UsernameNotFoundException::class)
    override fun loadUserByUsername(userId: String): UserDetails {
        val (id, _, password) = userMapper.findOne(userId) ?: throw UsernameNotFoundException(userId)
        var role = 0
        for (rightstr in userRoleMapper.findAllByUserId(id!!)) {
            when (rightstr.toLowerCase()) {
                "administrator" -> role = 2
                "source_browser" -> role = Math.max(role, 1)
                "news_publisher" -> {
                    // TODO
                }
            }
        }

        return org.springframework.security.core.userdetails.User.withUsername(userId).password(password!!).authorities(ROLES[role]).build()
    }

    companion object {

        private val ROLE_ADMIN = ImmutableList.copyOf(AuthorityUtils.createAuthorityList("ROLE_ADMIN", "ROLE_SOURCE_BROWSER", "ROLE_USER"))
        private val ROLE_SOURCE_BROWSER = ImmutableList.copyOf(AuthorityUtils.createAuthorityList("ROLE_SOURCE_BROWSER", "ROLE_USER"))
        private val ROLE_USER = ImmutableList.copyOf(AuthorityUtils.createAuthorityList("ROLE_USER"))
        private val ROLES = ImmutableList.of<List<GrantedAuthority>>(ROLE_USER, ROLE_SOURCE_BROWSER, ROLE_ADMIN)

        fun isAdminLoginned(request: HttpServletRequest): Boolean {
            return request.isUserInRole("ADMIN")
        }

        fun isSourceBrowser(request: HttpServletRequest): Boolean {
            return request.isUserInRole("SOURCE_BROWSER")
        }

        fun isUser(authentication: Authentication?, userId: String?): Boolean {
            return userId != null && authentication?.name == userId
        }
    }

}
