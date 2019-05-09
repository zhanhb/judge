/*
 * Copyright 2015 zhanhb.
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

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Configuration
import javax.servlet.ServletContext

/**
 *
 * @author zhanhb
 */


@Configuration
class StartUpConfiguration {

    @Autowired
    fun setStartUpDate(servlet: ServletContext, application: ApplicationContext) {
        servlet.setAttribute("startupDate", application.startupDate)
        log.debug("{}: {}", "javax.servlet.context.tempdir", servlet.getAttribute("javax.servlet.context.tempdir"))
    }

    companion object {
        private val log = LoggerFactory.getLogger(StartUpConfiguration::class.java)
    }
}
