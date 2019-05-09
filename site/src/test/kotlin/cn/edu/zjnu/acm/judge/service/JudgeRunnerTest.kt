/*
 * Copyright 2017-2019 ZJNU ACM.
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
import cn.edu.zjnu.acm.judge.core.GroovyHolder
import cn.edu.zjnu.acm.judge.core.SimpleValidator
import cn.edu.zjnu.acm.judge.domain.Language
import cn.edu.zjnu.acm.judge.mapper.LanguageMapper
import cn.edu.zjnu.acm.judge.support.JudgeData
import cn.edu.zjnu.acm.judge.support.RunRecord
import cn.edu.zjnu.acm.judge.util.PlatformAssuming.assumingWindows
import cn.edu.zjnu.acm.judge.util.ResultType
import com.google.common.collect.ImmutableMap
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.Arguments.arguments
import org.junit.jupiter.params.provider.MethodSource
import org.junit.platform.runner.JUnitPlatform
import org.junit.runner.RunWith
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.web.WebAppConfiguration
import org.springframework.transaction.annotation.Transactional
import java.io.File
import java.io.IOException
import java.net.URISyntaxException
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*
import kotlin.streams.asSequence

/**
 * @author zhanhb
 */
@RunWith(JUnitPlatform::class)
@SpringBootTest(classes = [Application::class])
@Transactional
@WebAppConfiguration
class JudgeRunnerTest @Throws(URISyntaxException::class, IOException::class)
constructor() {

    @Suppress("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private val languageMapper: LanguageMapper? = null
    @Autowired
    private val judgeRunner: JudgeRunner? = null
    @Autowired
    private val submissionService: SubmissionService? = null

    private val timeLimit = 6000L
    private val memoryLimit = 256 * 1024L
    private val validator = SimpleValidator.PE_AS_ACCEPTED

    private val judgeData = JudgeData(Paths.get(JudgeRunnerTest::class.java.getResource("/sample/data").toURI()))

    private fun findFirstLanguageByExtension(extension: String): Int {
        return languageMapper!!.findAll().stream()
                .asSequence()
                .filter { language -> language.sourceExtension!!.toLowerCase() == extension.toLowerCase() }
                .map { it.id }
                .firstOrNull() ?: throw RuntimeException()
    }

    @ParameterizedTest(name = "{index}: {0}")
    @MethodSource("data")
    @Throws(IOException::class)
    fun test(key: String, checker: Checker, path: Path) {
        val work = Files.createDirectories(Paths.get("target/work/judgeRunnerTest").resolve(key))
        val groovyJars = GroovyHolder.paths
        assertThat(groovyJars).isNotEmpty()
        for (groovyJar in groovyJars) {
            Files.copy(groovyJar, work.resolve(groovyJar.getFileName().toString()))
        }
        val cp = groovyJars.joinToString(File.pathSeparator) { p -> p.fileName.toString() }
        val executeCommand = build("java", "-cp", cp, groovy.ui.GroovyMain::class.java.name, "Main.groovy")
        val groovy = Language(
                name = "groovy",
                sourceExtension = "groovy",
                executeCommand = executeCommand,
                executableExtension = "groovy",
                description = "",
                timeFactor = 2
        )
        log.warn("Language groovy: {}", groovy)
        languageMapper!!.save(groovy)

        val extension = getExtension(path)
        val languageId = findFirstLanguageByExtension(EXTENSION_MAP[extension]
                ?: throw RuntimeException("extension '$extension' not found"))
        val language = languageMapper.findOne(languageId.toLong())
                ?: throw RuntimeException("language $languageId not exists")
        val source = String(Files.readAllBytes(path), StandardCharsets.UTF_8)
        val runRecord = RunRecord(
                source = source,
                timeLimit = timeLimit,
                memoryLimit = memoryLimit,
                language = language
        )

        val runResult = judgeRunner!!.run(runRecord, work, judgeData, validator, false)

        val expectScore = SPECIAL_SCORE[key] ?: checker.score
        val expectedCaseResult = ResultType.getCaseScoreDescription(checker.status)

        assertThat(runResult.type)
                .withFailMessage("type will either be null or COMPILATION_ERROR," +
                        " if got other result, please modify this file")
                .isNull()
        val detail1 = runResult.detail
        val details = if (detail1 != null) submissionService!!.parseSubmissionDetail(detail1) else null
        val msg = "%s %s %s"
        val param = arrayOf(key, details, expectedCaseResult)
        assertThat(runResult.score).withFailMessage(msg, *param).isEqualTo(expectScore)
        assertThat(details).withFailMessage(msg, *param)
                .anyMatch { (result) -> expectedCaseResult == result }
    }

    companion object {

        private val log = LoggerFactory.getLogger(JudgeRunnerTest::class.java)
        private val EXTENSION_MAP = ImmutableMap.of("cpp", "cc", "groovy", "groovy", "c", "c")

        private val SPECIAL_SCORE = ImmutableMap.of("wa/less.groovy", 50)

        private fun build(vararg args: String): String {
            return Arrays.stream(args)
                    .asSequence()
                    .map { s -> if (StringTokenizer(s).countTokens() > 1) '"'.toString() + s + '"'.toString() else s }
                    .joinToString(" ")
        }

        private fun getExtension(path: Path): String {
            val name = path.fileName.toString()
            return if (name.lastIndexOf('.') > 0) name.substring(name.lastIndexOf('.') + 1) else ""
        }

        @JvmStatic
        @Throws(Exception::class)
        fun data(): List<Arguments> {
            assumingWindows()
            val list = ArrayList<Arguments>(20)
            val program = Paths.get(JudgeRunnerTest::class.java.getResource("/sample/program").toURI())
            for (c in Checker.values()) {
                val dir = program.resolve(c.name)

                Files.list(dir).use { stream -> stream.forEach { path -> list.add(arguments(c.name + "/" + path.fileName, c, path)) } }
            }
            return list
        }
    }

}
