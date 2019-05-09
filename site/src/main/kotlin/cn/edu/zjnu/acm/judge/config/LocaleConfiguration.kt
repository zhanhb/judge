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

import javax.servlet.ServletContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.LocaleResolver
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import org.springframework.web.servlet.i18n.CookieLocaleResolver
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor

/**
 *
 * @author zhanhb
 */
@Configuration
class LocaleConfiguration : WebMvcConfigurer {

    override fun addInterceptors(registry: InterceptorRegistry) {
        val localeChangeInterceptor = LocaleChangeInterceptor()
        localeChangeInterceptor.paramName = "lang"
        localeChangeInterceptor.isIgnoreInvalidLocale = true
        registry.addInterceptor(localeChangeInterceptor)
    }

    /* Store preferred language configuration in a cookie */
    @Bean(name = ["localeResolver"])
    fun localeResolver(container: ServletContext): LocaleResolver {
        val localeResolver = CookieLocaleResolver()
        localeResolver.cookieName = "locale"
        localeResolver.cookieMaxAge = 15 * 24 * 60 * 60 // 15 day
        localeResolver.cookiePath = getCookiePath(container)
        localeResolver.isLanguageTagCompliant = true
        return localeResolver
    }

    private fun getCookiePath(container: ServletContext): String {
        val contextPath = container.contextPath
        return if (contextPath.endsWith("/")) contextPath else "$contextPath/"
    }

}
