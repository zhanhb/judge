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
import cn.edu.zjnu.acm.judge.domain.Contest
import cn.edu.zjnu.acm.judge.domain.Problem
import cn.edu.zjnu.acm.judge.domain.User
import cn.edu.zjnu.acm.judge.service.LocaleService
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.platform.runner.JUnitPlatform
import org.junit.runner.RunWith
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.web.WebAppConfiguration
import org.springframework.transaction.annotation.Transactional
import java.util.*

/**
 *
 * @author zhanhb
 */
@RunWith(JUnitPlatform::class)
@SpringBootTest(classes = [Application::class])
@Transactional
@WebAppConfiguration
class ContestMapperTest {

    @Suppress("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private val instance: ContestMapper? = null
    @Autowired
    private val localeService: LocaleService? = null
    private val locale = Locale.SIMPLIFIED_CHINESE

    /**
     * Test of standing method, of class ContestMapper.
     */
    @Test
    fun testStanding() {
        log.info("standing")
        val contest = 1058L
        val result = instance!!.standing(contest)
        log.info("{}", result)
    }

    /**
     * Test of getProblems method, of class ContestMapper.
     */
    @Test
    fun testGetProblems() {
        log.info("getProblems")
        val contestId = 0L
        val expResult = Arrays.asList<Problem>()
        val result = instance!!.getProblems(contestId, null, localeService!!.resolve(locale))
        assertThat(result).isEqualTo(expResult)
    }

    /**
     * Test of getProblems method, of class ContestMapper.
     */
    @Test
    fun testGetProblemsNullLocale() {
        log.info("getProblems")
        val contestId = 0L
        val expResult = Arrays.asList<Problem>()
        val result = instance!!.getProblems(contestId, null, null)
        assertThat(result).isEqualTo(expResult)
    }

    /**
     * Test of getProblems method, of class ContestMapper.
     */
    @Test
    fun testGetUserProblems() {
        log.info("getProblems")
        val contestId = 0L
        val userId = "'"
        val expResult = Arrays.asList<Problem>()
        val result = instance!!.getProblems(contestId, userId, localeService!!.resolve(locale))
        assertThat(result).isEqualTo(expResult)
    }

    /**
     * Test of findOne method, of class ContestMapper.
     */
    @Test
    fun testFindOne() {
        log.info("findOne")
        val contestId = 0L
        val expResult: Contest? = null
        val result = instance!!.findOne(contestId)
        assertThat(result).isEqualTo(expResult)
    }

    /**
     * Test of getProblem method, of class ContestMapper.
     */
    @Test
    fun testGetProblem() {
        log.info("getProblem")
        val contestId = 0L
        val problemOrder = 0L
        val expResult: Problem? = null
        val result = instance!!.getProblem(contestId, problemOrder, locale.toLanguageTag())
        assertThat(result).isEqualTo(expResult)
    }

    /**
     * Test of findOneByIdAndNotDisabled method, of class ContestMapper.
     */
    @Test
    fun testFindOneByIdAndDisabledFalse() {
        log.info("findOneByIdAndDisabledFalse")
        val contestId = 0L
        val expResult: Contest? = null
        val result = instance!!.findOneByIdAndNotDisabled(contestId)
        assertThat(result).isEqualTo(expResult)
    }

    /**
     * Test of attenders method, of class ContestMapper.
     */
    @Test
    fun testAttenders() {
        log.info("attenders")
        val contestId = 0L
        val expResult = Arrays.asList<User>()
        val result = instance!!.attenders(contestId)
        assertThat(result).isEqualTo(expResult)
    }

    companion object {
        private val log = LoggerFactory.getLogger(ContestMapperTest::class.java)
    }
}
