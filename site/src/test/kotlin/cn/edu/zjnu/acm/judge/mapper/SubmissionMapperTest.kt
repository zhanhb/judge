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
import cn.edu.zjnu.acm.judge.data.form.BestSubmissionForm
import cn.edu.zjnu.acm.judge.data.form.SubmissionQueryForm
import cn.edu.zjnu.acm.judge.util.Pageables
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
class SubmissionMapperTest {

    @Autowired
    private val instance: SubmissionMapper? = null

    /**
     * Test of findAllByCriteria method, of class SubmissionMapper.
     */
    @Test
    fun testFindAllByCriteria() {
        log.info("findAllByCriteria")
        val submissionCriteria = SubmissionQueryForm(
                contest = 1058L,
                problem = 1449L,
                size = 50
        )
        val result = instance!!.findAllByCriteria(submissionCriteria)
        log.info("{}", result.size)
    }

    /**
     * Test of bestSubmission method, of class SubmissionMapper.
     */
    @Test
    fun testBestSubmission() {
        log.info("bestSubmission")
        val problemId: Long = 1000
        val form = BestSubmissionForm(problemId = problemId)
        for (pageable in Pageables.bestSubmission()) {
            instance!!.bestSubmission(form, pageable)
        }
    }

    companion object {
        private val log = LoggerFactory.getLogger(SubmissionMapperTest::class.java)
    }
}
