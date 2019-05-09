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
package cn.edu.zjnu.acm.judge.util

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Test
import org.junit.platform.runner.JUnitPlatform
import org.junit.runner.RunWith
import org.slf4j.LoggerFactory
import org.springframework.transaction.annotation.Transactional
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Paths

/**
 *
 * @author zhanhb
 */
@RunWith(JUnitPlatform::class)
@Transactional
class DeleteHelperTest {

    /**
     * Test of delete method, of class DeleteHelper.
     *
     * @throws java.io.IOException
     */
    @Test
    @Throws(IOException::class)
    fun testDelete() {
        log.info("delete")
        val path = Paths.get("target/test1.txt")
        Files.createFile(path)
        assertFalse(Files.notExists(path))
        DeleteHelper.delete(path)
        assertFalse(Files.exists(path))
    }

    /**
     * Test of delete method, of class DeleteHelper.
     *
     * @throws java.io.IOException
     */
    @Test
    @Throws(IOException::class)
    fun testDeleteDirectory() {
        log.info("delete")
        val d = Paths.get("target/test1.txt")
        val path = d.resolve("target/test1.txt")
        Files.createDirectories(path.parent)
        Files.createFile(path)
        DeleteHelper.delete(d)
        assertFalse(Files.exists(d))
    }

    @Test
    @Throws(IOException::class)
    fun testNotExists() {
        DeleteHelper.delete(Paths.get("target/test1"))
    }

    @Test
    @Throws(IOException::class)
    fun testDeleteEmptyDirectory() {
        log.info("delete")
        val d = Paths.get("target/test1.txt")
        Files.createDirectories(d)
        DeleteHelper.delete(d)
        assertFalse(Files.exists(d))
    }

    companion object {
        private val log = LoggerFactory.getLogger(DeleteHelperTest::class.java)
    }
}
