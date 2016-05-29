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
package cn.edu.zjnu.acm.judge.config;

import cn.edu.zjnu.acm.judge.domain.LoginLog;
import cn.edu.zjnu.acm.judge.mapper.LoginlogMapper;
import cn.edu.zjnu.acm.judge.mapper.UserMapper;
import cn.edu.zjnu.acm.judge.service.UserDetailService;
import cn.edu.zjnu.acm.judge.user.PasswordConfuser;
import java.io.IOException;
import java.net.URLEncoder;
import java.time.Instant;
import java.util.Optional;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

/**
 *
 * @author zhanhb
 */
@Configuration
@Slf4j
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserDetailService userDetailService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private PersistentTokenRepository persistentTokenRepository;
    @Autowired
    private LoginlogMapper loginlogMapper;
    @Autowired
    private PasswordConfuser passwordConfuser;
    @Autowired
    private UserMapper userMapper;

    private void saveLoginLog(HttpServletRequest request, boolean success) {
        String userId = Optional.ofNullable(request.getParameter("user_id1")).orElse("");
        String passsword = Optional.ofNullable(request.getParameter("password1")).orElse("");
        loginlogMapper.save(LoginLog.builder()
                .user(userId)
                .password(passwordConfuser.confuse(passsword))
                .ip(request.getRemoteAddr())
                .success(success)
                .build());
        if (success) {
            Optional.ofNullable(userMapper.findOne(userId)).ifPresent(user -> {
                userMapper.update(
                        user.toBuilder()
                        .accesstime(Instant.now())
                        .ip(request.getRemoteAddr())
                        .build());
            });
        }
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        SimpleUrlAuthenticationSuccessHandler simpleUrlAuthenticationSuccessHandler = new SimpleUrlAuthenticationSuccessHandler("/") {

            @Override
            public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
                    throws IOException, ServletException {
                saveLoginLog(request, true);
                super.onAuthenticationSuccess(request, response, authentication);
            }

        };
        simpleUrlAuthenticationSuccessHandler.setUseReferer(false);
        simpleUrlAuthenticationSuccessHandler.setTargetUrlParameter("url");
        DefaultRedirectStrategy defaultRedirectStrategy = new DefaultRedirectStrategy();

        defaultRedirectStrategy.setContextRelative(true);

        simpleUrlAuthenticationSuccessHandler.setRedirectStrategy(defaultRedirectStrategy);

        SimpleUrlLogoutSuccessHandler simpleUrlLogoutSuccessHandler = new SimpleUrlLogoutSuccessHandler();
        simpleUrlLogoutSuccessHandler.setUseReferer(true);

        // @formatter:off
        http
            .csrf()
                .disable()
            .formLogin()
                .loginPage("/login")
                .usernameParameter("user_id1")
                .passwordParameter("password1")
                .successHandler(simpleUrlAuthenticationSuccessHandler)
                .failureHandler(new SimpleUrlAuthenticationFailureHandler("/login?error") {
                    {
                        RedirectStrategy redirectStrategy = getRedirectStrategy();
                        setRedirectStrategy((request, response, url) -> {
                            String url1 = request.getParameter("url");
                            if (url1 != null) {
                                redirectStrategy.sendRedirect(request, response, url + "&url=" + URLEncoder.encode(url1, "UTF-8"));
                            } else {
                                redirectStrategy.sendRedirect(request, response, url);
                            }
                        });
                    }

                    @Override
                    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception)
                            throws IOException, ServletException {
                        saveLoginLog(request, false);
                        super.onAuthenticationFailure(request, response, exception);
                    }

                })
                .and()
            .headers()
                .disable()
            .logout()
                .logoutUrl("/logout")
                .logoutSuccessHandler(simpleUrlLogoutSuccessHandler)
                .and()
            .rememberMe()
                .rememberMeParameter("rememberMe")
                .tokenRepository(persistentTokenRepository)
                .and()
            .servletApi();
        // @formatter:on
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailService::getUserModel)
                .passwordEncoder(passwordEncoder);
    }

}
