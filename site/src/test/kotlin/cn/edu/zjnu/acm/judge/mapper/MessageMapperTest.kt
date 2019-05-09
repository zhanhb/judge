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
import cn.edu.zjnu.acm.judge.domain.Message
import org.assertj.core.api.Assertions.assertThat
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
class MessageMapperTest {

    @Autowired
    private val instance: MessageMapper? = null

    /**
     * Test of nextId method, of class MessageMapper.
     */
    @Test
    fun testNextId() {
        log.info("nextId")
        instance!!.nextId()
    }

    /**
     * Test of findOne method, of class MessageMapper.
     * [MessageMapper.findOne]
     */
    @Test
    fun testFindOne() {
        log.info("findOne")
        val id: Long = 0
        val expResult: Message? = null
        val result = instance!!.findOne(id)
        assertThat(result).isEqualTo(expResult)
    }

    /**
     * Test of findAllByThreadIdAndOrderNumGreaterThanOrderByOrderNum method, of
     * class MessageMapper.
     * [MessageMapper.findAllByThreadIdAndOrderNumGreaterThanOrderByOrderNum]
     */
    @Test
    fun testFindAllByThreadIdAndOrderNumGreaterThanOrderByOrderNum() {
        log.info("findAllByThreadIdAndOrderNumGreaterThanOrderByOrderNum")
        val thread: Long = -1
        val orderNum: Long = -1
        instance!!.findAllByThreadIdAndOrderNumGreaterThanOrderByOrderNum(thread, orderNum)
    }

    /**
     * Test of updateOrderNumByThreadIdAndOrderNumGreaterThan method, of class
     * MessageMapper.
     * [MessageMapper.updateOrderNumByThreadIdAndOrderNumGreaterThan]
     */
    @Test
    fun testUpdateOrderNumByThreadIdAndOrderNumGreaterThan() {
        log.info("updateOrderNumByThreadIdAndOrderNumGreaterThan")
        val thread = -1L
        val orderNum = -1L
        instance!!.updateOrderNumByThreadIdAndOrderNumGreaterThan(thread, orderNum)
    }

    /**
     * Test of updateThreadIdByThreadId method, of class MessageMapper.
     * [MessageMapper.updateThreadIdByThreadId]
     */
    @Test
    fun testUpdateThreadIdByThreadId() {
        log.info("updateThreadIdByThreadId")
        val nextId: Long = -1
        val original: Long = -1
        instance!!.updateThreadIdByThreadId(nextId, original)
    }

    /**
     * Test of findAllByThreadIdBetween method, of class MessageMapper.
     * [MessageMapper.findAllByThreadIdBetween]
     */
    @Test
    fun testFindAllByThreadIdBetween() {
        log.info("findAllByThreadIdBetween")
        val arr = arrayOf(-1L, 0L, 1L, null)
        val array = arrayOf(0, 1, -1, null)
        for (min in arr) {
            for (max in arr) {
                for (problemId in arr) {
                    for (limit in array) {
                        if (limit != -1) {
                            instance!!.findAllByThreadIdBetween(min, max, problemId, limit)
                        }
                    }
                }
            }
        }
    }

    /**
     * Test of mint method, of class MessageMapper.
     * [MessageMapper.mint]
     */
    @Test
    fun testMint() {
        log.info("mint")
        val top = 0L
        val problemId: Long? = null
        val limit = 0
        val coalesce = 0L
        val expResult = 0L
        val result = instance!!.mint(top, problemId, limit, coalesce)
        assertThat(result).isEqualTo(expResult)
    }

    /**
     * Test of maxt method, of class MessageMapper.
     * [MessageMapper.maxt]
     */
    @Test
    fun testMaxt() {
        log.info("maxt")
        val top = 0L
        val problemId: Long? = null
        val limit = 0
        val coalesce = 0L
        val expResult = 0L
        val result = instance!!.maxt(top, problemId, limit, coalesce)
        assertThat(result).isEqualTo(expResult)
    }

    companion object {
        private val log = LoggerFactory.getLogger(MessageMapperTest::class.java)
    }
}
