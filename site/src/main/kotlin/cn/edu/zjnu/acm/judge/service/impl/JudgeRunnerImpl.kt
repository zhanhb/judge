/*
 * Copyright 2019 ZJNU ACM.
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
package cn.edu.zjnu.acm.judge.service.impl

import cn.edu.zjnu.acm.judge.core.JudgeBridge
import cn.edu.zjnu.acm.judge.core.Options
import cn.edu.zjnu.acm.judge.core.Status
import cn.edu.zjnu.acm.judge.core.Validator
import cn.edu.zjnu.acm.judge.sandbox.win32.ProcessCreationHelper
import cn.edu.zjnu.acm.judge.service.DeleteService
import cn.edu.zjnu.acm.judge.service.JudgeRunner
import cn.edu.zjnu.acm.judge.support.JudgeData
import cn.edu.zjnu.acm.judge.support.RunRecord
import cn.edu.zjnu.acm.judge.support.RunResult
import cn.edu.zjnu.acm.judge.util.Platform
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.util.StringUtils
import java.io.*
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.text.DecimalFormat
import java.util.concurrent.TimeUnit
import java.util.stream.Collectors
import javax.annotation.PostConstruct
import javax.annotation.PreDestroy
import kotlin.streams.asSequence

/**
 * @author zhanhb
 */
@Service("judgeRunner")
class JudgeRunnerImpl(
        private val deleteService: DeleteService
) : JudgeRunner {

    private var judgeBridge: JudgeBridge? = null

    @PostConstruct
    fun init() {
        judgeBridge = JudgeBridge()
    }

    @PreDestroy
    fun shutdown() {
        judgeBridge!!.close()
    }

    private fun delete(path: Path) {
        deleteService.delete(path)
    }

    override fun run(runRecord: RunRecord, workDirectory: Path, judgeData: JudgeData,
                     validator: Validator, cleanDirectory: Boolean): RunResult {
        try {
            val source = runRecord.source

            if (source.isNullOrEmpty()) {
                return RunResult(type = Status.COMPILATION_ERROR, compileInfo = "empty source file")
            }
            val main = "Main"
            Files.createDirectories(workDirectory)
            val language = runRecord.language!!
            val sourceFile = workDirectory.resolve(main + "." + language.sourceExtension) //源码码文件
            Files.write(sourceFile, source.toByteArray(Platform.charset))

            val compileCommand = language.compileCommand
            log.debug("Compile Command: {}", compileCommand)

            val compileInfoTxt: String?
            if (StringUtils.hasText(compileCommand)) {
                // create compiling process
                // VC++ will output compiling info to stdout
                // G++ will output compiling info to stderr
                val compileInfo = workDirectory.resolve("compileInfo.txt")
                val pb = ProcessBuilder(*compileCommand!!.split("\\s+".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray())
                        .directory(workDirectory.toFile())
                        .redirectInput(ProcessBuilder.Redirect.from(NULL_FILE))
                        .redirectOutput(compileInfo.toFile())
                        .redirectErrorStream(true)
                val process = ProcessCreationHelper.execute<Process> { pb.start() }
                try {
                    process.waitFor(1, TimeUnit.MINUTES)
                } catch (ex: InterruptedException) {
                    throw InterruptedIOException()
                }

                // export compiling infomation
                val errorInfo: String
                if (process.isAlive()) {
                    process.destroyForcibly()
                    try {
                        process.waitFor()
                    } catch (ex: InterruptedException) {
                        throw InterruptedIOException()
                    }

                    errorInfo = "Compile timeout\nOutput:\n" + collectLines(compileInfo)
                } else {
                    errorInfo = collectLines(compileInfo)
                }
                compileInfoTxt = errorInfo
                log.debug("errorInfo = {}", errorInfo)
                // The executable file after compiling
                val executable = workDirectory.resolve(main + "." + language.executableExtension)
                log.debug("executable = {}", executable)
                val compilePassed = Files.exists(executable)
                if (!compilePassed) {
                    return RunResult(compileInfo = compileInfoTxt, type = Status.COMPILATION_ERROR)
                }
            } else {
                compileInfoTxt = null
            }
            val caseNum = judgeData.caseCount
            val details = ArrayList<String>(caseNum shl 2)
            val cmd = language.executeCommand

            // executable command should be absolute
            val command = if (!cmd.isNullOrBlank()) cmd else workDirectory.toAbsolutePath().resolve("Main." + language.executableExtension).toString()
            val extTime = language.extTime
            val castTimeLimit = runRecord.timeLimit * language.timeFactor + extTime
            val extraMemory = language.extMemory //内存附加
            val caseMemoryLimit = (runRecord.memoryLimit + extraMemory) * 1024
            val opts = (0 until caseNum).map {
                val entry = judgeData[it]
                val `in` = entry.first
                val standard = entry.second
                val progOutput = workDirectory.resolve(standard.getFileName())

                Options(
                        timeLimit = castTimeLimit, // time limit
                        memoryLimit = caseMemoryLimit, // memory in bytes
                        outputLimit = 16L * 1024 * 1024, // 16M
                        command = command,
                        workDirectory = workDirectory,
                        inputFile = `in`,
                        outputFile = progOutput,
                        standardOutput = standard,
                        errFile = NULL_FILE.toPath()
                )
            }
            val scorePerCase = DecimalFormat("0.#").format(100.0 / caseNum)
            var time: Long = 0
            var memory: Long = 0
            var accept = 0 // final case who's result is accepted.
            val ers = judgeBridge!!.judge(opts, false, validator)
            for (er in ers) {
                val tim1 = Math.max(0, er.time - extTime)
                val mem1 = Math.max(0, er.memory / 1024 - extraMemory)
                val message = er.message
                val success = er.isSuccess
                time = Math.max(time, tim1)
                memory = Math.max(memory, mem1)
                log.debug("message = {}, time = {}, memory = {}", message, time, memory)

                details.add(er.code.result.toString())
                details.add(if (success) scorePerCase else "0")
                details.add(tim1.toString())
                details.add(mem1.toString())
                if (success) {
                    ++accept
                }
            }
            log.debug("{}", details)
            var score = if (accept >= 0) Math.round(accept * 100.0 / caseNum).toInt() else accept
            if (score == 0 && accept != 0) {
                ++score
            } else if (score == 100 && accept != caseNum) {
                --score
            }
            val msg = details.stream().map({ it.toString() }).collect(Collectors.joining(","))
            return RunResult(compileInfo = compileInfoTxt, score = score, time = time, memory = memory, detail = msg)
        } catch (ex: IOException) {
            throw UncheckedIOException(ex)
        } finally {
            if (cleanDirectory) {
                delete(workDirectory)
            }
        }
    }

    companion object {

        private val log = LoggerFactory.getLogger(JudgeRunnerImpl::class.java)
        private val NULL_FILE = Paths.get(if (Platform.isWindows) "NUL" else "/dev/null").toFile()

        @Throws(IOException::class)
        private fun collectLines(path: Path): String {
            val charset = Platform.charset
            var compileInfo: String = ""
            Files.newInputStream(path).use { `is`
                ->
                InputStreamReader(`is`, charset).use { isr
                    ->
                    BufferedReader(isr).use { br -> compileInfo = br.lines().asSequence().joinToString("\n") }
                }
            }
            return if (compileInfo.length > 1000) compileInfo.substring(0, 997) + "..." else compileInfo
        }
    }

}