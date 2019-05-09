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
class UserProblemMapperTest {

    @Suppress("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private val instance: UserProblemMapper? = null

    /**
     * Test of init method, of class UserProblemMapper.
     */
    @Test
    fun testInit() {
        log.info("init")
        instance!!.init()
    }

    /**
     * Test of findOne method, of class UserProblemMapper.
     */
    @Test
    fun testFindOne() {
        log.info("findOne")
        val userId = "coach"
        val problemId: Long = 1000
        instance!!.findOne(userId, problemId)
    }

    /**
     * Test of update method, of class UserProblemMapper.
     */
    @Test
    fun testUpdate() {
        log.info("update")
        val userId = "coach"
        val problemId = 1000L
        instance!!.update(userId, problemId)
    }

    /**
     * Test of updateUser method, of class UserProblemMapper.
     */
    @Test
    fun testUpdateUser() {
        log.info("updateUser")
        val userId = "coach"
        instance!!.updateUser(userId)
    }

    /**
     * Test of updateProblem method, of class UserProblemMapper.
     */
    @Test
    fun testUpdateProblem() {
        log.info("updateProblem")
        val problemId: Long = 1000
        instance!!.updateProblem(problemId)
    }

    /**
     * Test of updateUsers method, of class UserProblemMapper.
     */
    @Test
    fun testUpdateUsers() {
        log.info("updateUsers")
        instance!!.updateUsers()
    }

    /**
     * Test of updateProblems method, of class UserProblemMapper.
     */
    @Test
    fun testUpdateProblems() {
        log.info("updateProblems")
        instance!!.updateProblems()
    }

    /**
     * Test of findAllSolvedByUserId method, of class UserProblemMapper.
     */
    @Test
    fun testFindAllByUserIdAndAcceptedNot0() {
        log.info("findAllByUserIdAndAcceptedNot0")
        val userId = ""
        instance!!.findAllSolvedByUserId(userId)
    }

    /**
     * Test of findAllByUserId method, of class UserProblemMapper.
     */
    @Test
    fun testFindAllByUserId() {
        log.info("findAllByUserId")
        val userId = ""
        instance!!.findAllByUserId(userId)
    }

    companion object {
        private val log = LoggerFactory.getLogger(UserProblemMapperTest::class.java)
    }
}
