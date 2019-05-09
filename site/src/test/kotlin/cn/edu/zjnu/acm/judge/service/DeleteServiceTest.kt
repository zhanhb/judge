/*
 * Copyright 2018 ZJNU ACM.
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
package cn.edu.zjnu.acm.judge.service

import cn.edu.zjnu.acm.judge.Application
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.platform.runner.JUnitPlatform
import org.junit.runner.RunWith
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.web.WebAppConfiguration
import org.springframework.transaction.annotation.Transactional
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Paths
import java.util.concurrent.TimeUnit

/**
 *
 * @author zhanhb
 */
@RunWith(JUnitPlatform::class)
@SpringBootTest(classes = [Application::class])
@Transactional
@WebAppConfiguration
class DeleteServiceTest {

    @Autowired
    private val instance: DeleteService? = null

    /**
     * Test of delete method, of class DeleteService.
     */
    @Test
    @Throws(IOException::class, InterruptedException::class)
    fun testDelete() {
        log.info("delete")
        val path = Files.createTempDirectory(Paths.get("target"), "tmp")
        val txt = Files.createFile(path.resolve("a.txt"))
        val future = instance!!.delete(path)
        while (!future.isDone) {
            TimeUnit.MILLISECONDS.sleep(200)
        }
        assertTrue(!Files.exists(txt))
    }

    companion object {
        private val log = LoggerFactory.getLogger(DeleteServiceTest::class.java)
    }
}
