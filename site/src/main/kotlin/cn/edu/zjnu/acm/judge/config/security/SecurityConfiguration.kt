/*
 * Copyright 2016-2019 ZJNU ACM.
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
package cn.edu.zjnu.acm.judge.config.security

import cn.edu.zjnu.acm.judge.util.URLEncoder
import com.github.zhanhb.ckfinder.connector.autoconfigure.CKFinderProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.security.web.DefaultRedirectStrategy
import org.springframework.security.web.RedirectStrategy
import org.springframework.security.web.authentication.AuthenticationFailureHandler
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository
import org.springframework.security.web.savedrequest.NullRequestCache
import java.io.IOException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 *
 * @author zhanhb
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true)
class SecurityConfiguration(
        private val userDetailsService: UserDetailsService,
        private val passwordEncoder: PasswordEncoder,
        private val persistentTokenRepository: PersistentTokenRepository,
        private val ckfinderProperties: CKFinderProperties
) : WebSecurityConfigurerAdapter() {

    @Bean(name = ["authenticationManager"])
    @Primary
    @Throws(Exception::class)
    override fun authenticationManagerBean(): AuthenticationManager {
        return super.authenticationManagerBean()
    }

    @Throws(Exception::class)
    override fun configure(http: HttpSecurity) {
        val simpleUrlAuthenticationSuccessHandler = SimpleUrlAuthenticationSuccessHandler("/")
        simpleUrlAuthenticationSuccessHandler.setUseReferer(false)
        simpleUrlAuthenticationSuccessHandler.setTargetUrlParameter("url")
        val defaultRedirectStrategy = DefaultRedirectStrategy()

        simpleUrlAuthenticationSuccessHandler.setRedirectStrategy(defaultRedirectStrategy)

        val simpleUrlLogoutSuccessHandler = SimpleUrlLogoutSuccessHandler()
        simpleUrlLogoutSuccessHandler.setUseReferer(true)

        // @formatter:off
        http
            .authorizeRequests()
                .antMatchers(*ckfinderProperties.servlet.path).hasAnyRole("ADMIN")
                .and()
            .csrf()
                .disable()
            .exceptionHandling()
                .authenticationEntryPoint(authenticationEntryPoint())
                .and()
            .formLogin()
                .loginPage("/login")
                .usernameParameter("user_id1")
                .passwordParameter("password1")
                .successHandler(simpleUrlAuthenticationSuccessHandler)
                .failureHandler(failureHandler())
                .permitAll()
                .and()
            .headers()
                .cacheControl().disable()
                .httpStrictTransportSecurity().disable()
                .frameOptions().sameOrigin()
                .and()
            .logout()
                .logoutUrl("/logout.html")
                .logoutSuccessHandler(simpleUrlLogoutSuccessHandler)
                .permitAll()
                .and()
            .rememberMe()
                .rememberMeParameter("rememberMe")
                .tokenRepository(persistentTokenRepository)
                .and()
            .requestCache()
                .requestCache(NullRequestCache())
                .and()
            .servletApi()
        // @formatter:on
    }

    @Throws(Exception::class)
    override fun configure(auth: AuthenticationManagerBuilder) {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder)
    }

    @Bean
    fun authenticationEntryPoint(): AuthenticationEntryPoint {
        return AuthenticationEntryPoint { request, response, _ ->
            request.getRequestDispatcher("/unauthorized").forward(request, response)
        }
    }

    private fun failureHandler(): AuthenticationFailureHandler {
        val handler = SimpleUrlAuthenticationFailureHandler()
        handler.setAllowSessionCreation(false)
        handler.setDefaultFailureUrl("/login?error")
        handler.setRedirectStrategy(FailureRedirectStrategy())
        return handler
    }

    companion object {

        private class FailureRedirectStrategy : RedirectStrategy {

            private val redirectStrategy: RedirectStrategy = DefaultRedirectStrategy()

            @Throws(IOException::class)
            override fun sendRedirect(request: HttpServletRequest, response: HttpServletResponse, url: String) {
                val url1: String? = request.getParameter("url")
                if (url1.isNullOrBlank()) {
                    redirectStrategy.sendRedirect(request, response, url)
                } else {
                    redirectStrategy.sendRedirect(request, response, url + (if (url.contains("?")) '&' else '?') + "url=" + URLEncoder.QUERY.encode(url1))
                }
            }
        }
    }

}
