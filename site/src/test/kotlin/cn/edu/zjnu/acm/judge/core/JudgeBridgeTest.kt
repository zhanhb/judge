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
package cn.edu.zjnu.acm.judge.core

import cn.edu.zjnu.acm.judge.util.DeleteHelper
import cn.edu.zjnu.acm.judge.util.PlatformAssuming.assumingWindows
import groovy.ui.GroovyMain
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.Arguments.arguments
import org.junit.jupiter.params.provider.MethodSource
import org.junit.platform.runner.JUnitPlatform
import org.junit.runner.RunWith
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardCopyOption
import java.util.*
import kotlin.streams.asSequence

/**
 * @author zhanhb
 */
@RunWith(JUnitPlatform::class)
class JudgeBridgeTest {

    private val validator = SimpleValidator.NORMAL

    private var judgeBridge: JudgeBridge? = null

    @BeforeEach
    fun setUp() {
        judgeBridge = JudgeBridge()
    }

    @AfterEach
    fun tearDown() {
        judgeBridge!!.close()
    }

    @ParameterizedTest
    @MethodSource("data")
    @Throws(IOException::class)
    fun test(checker: Checker, executable: String) {
        val options = Options(
                command = build(
                        "java",
                        "-cp",
                        groovy!!.fileName.toString(),
                        GroovyMain::class.java.name,
                        executable),
                inputFile = input,
                outputFile = work!!.resolve(output!!.fileName),
                standardOutput = output,
                errFile = work!!.resolve("NUL"),
                memoryLimit = 256L * 1024 * 1024,
                outputLimit = 16 * 1024 * 1024,
                workDirectory = work,
                timeLimit = 6000
        )
        val stopOnError = false
        val (_, _, result) = judgeBridge!!.judge(listOf(options), stopOnError, validator)[0]
        val expect = checker.status
        assertThat(result)
                .withFailMessage("executable: %s, exptect %s, got %s",
                        executable, expect, result)
                .isEqualTo(expect)
    }

    companion object {

        private var work: Path? = null
        private var input: Path? = null
        private var output: Path? = null
        private var groovy: Path? = null
        private var program: Path? = null

        private fun build(vararg args: String): String {
            return Arrays.stream(args)
                    .asSequence()
                    .map<String, String> { s -> if (StringTokenizer(s).countTokens() > 1) '"'.toString() + s + '"'.toString() else s }
                    .joinToString(" ")
        }

        @JvmStatic
        @Throws(Exception::class)
        fun data(): List<Arguments> {
            val values = Checker.values()
            val list = ArrayList<Arguments>(values.size)
            for (checker in values) {
                val path = program!!.resolve(checker.name)
                Files.list(path)
                        .asSequence()
                        .filter { p -> p.fileName.toString().endsWith(".groovy") }
                        .map { it.toString() }
                        .forEach { executable -> list.add(arguments(checker, executable)) }
            }
            return list
        }

        @BeforeAll
        @JvmStatic
        @Throws(Exception::class)
        fun setUpClass() {
            assumingWindows()
            work = Files.createDirectories(Paths.get("target", "work", "judgeBridgeTest"))
            val uri = JudgeBridgeTest::class.java.getResource("/sample/program").toURI()
            program = Paths.get(uri)
            val data = program!!.resolve("../data").toRealPath()
            input = data.resolve("b.in")
            output = data.resolve("b.out")
            val groovyJars = GroovyHolder.paths
            assertThat(groovyJars).withFailMessage("groovyJars").hasSize(1)
            val groovyPath = groovyJars[0]
            groovy = Files.copy(groovyPath, work!!.resolve(groovyPath.fileName.toString()), StandardCopyOption.REPLACE_EXISTING)
        }

        @AfterAll
        @JvmStatic
        @Throws(Exception::class)
        fun tearDownClass() {
            if (work != null) {
                DeleteHelper.delete(work!!)
            }
        }
    }

}
