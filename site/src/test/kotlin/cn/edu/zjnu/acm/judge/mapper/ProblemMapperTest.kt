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
package cn.edu.zjnu.acm.judge.mapper

import cn.edu.zjnu.acm.judge.Application
import org.junit.jupiter.api.Test
import org.junit.platform.runner.JUnitPlatform
import org.junit.runner.RunWith
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.web.WebAppConfiguration
import org.springframework.transaction.annotation.Transactional

/**
 *
 * @author zhanhb
 */
@RunWith(JUnitPlatform::class)
@SpringBootTest(classes = [Application::class])
@Transactional
@WebAppConfiguration
class ProblemMapperTest {

    @Suppress("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private val instance: ProblemMapper? = null
    private val lang = "en"

    /**
     * Test of findOne method, of class ProblemMapper.
     */
    @Test
    fun testFindOne() {
        log.debug("findOne")
        val locales = arrayOf(lang, null)
        for (locale in locales) {
            instance!!.findOne(0, locale)
            instance.findOne(1000, locale)
        }
    }

    /**
     * Test of findOne method, of class ProblemMapper.
     */
    @Test
    fun testFindOneNoI18n() {
        log.debug("findOne")
        instance!!.findOneNoI18n(0)
        instance.findOneNoI18n(1000)
    }

    /**
     * Test of touchI18n method, of class ProblemMapper.
     */
    @Test
    fun testTouchI18n() {
        log.info("touchI18n")
        val problemId = 0L
        instance!!.touchI18n(problemId, lang)
    }

    companion object {
        private val log = LoggerFactory.getLogger(ProblemMapperTest::class.java)
    }
}
