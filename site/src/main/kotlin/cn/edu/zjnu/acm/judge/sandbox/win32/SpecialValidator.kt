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
package cn.edu.zjnu.acm.judge.sandbox.win32

import cn.edu.zjnu.acm.judge.util.UnixLineEndingPrintWriter
import cn.edu.zjnu.acm.judge.core.Status
import cn.edu.zjnu.acm.judge.core.Validator
import org.slf4j.LoggerFactory
import java.io.File
import java.io.IOException
import java.io.InterruptedIOException
import java.nio.file.Path

/**
 * @author zhanhb
 */
class SpecialValidator(private val command: String, dir: Path) : Validator {

    private val dir: File = dir.toFile()

    @Throws(IOException::class)
    override fun validate(inputFile: Path?, standardOutput: Path?, outputFile: Path?): Status {
        log.debug("use special judge '{}'", command)
        val specialJudge = ProcessCreationHelper.execute<Process> { Runtime.getRuntime().exec(command, null, dir) }
        UnixLineEndingPrintWriter(specialJudge.outputStream).use { specialOut ->
            log.debug("{}", inputFile)
            specialOut.println(inputFile)
            log.debug("{}", standardOutput)
            specialOut.println(standardOutput)
            log.debug("{}", outputFile)
            specialOut.println(outputFile)
        }
        try {
            val specialExitValue = specialJudge.waitFor()
            log.debug("specialExitValue = {}", specialExitValue)
            return when {
                specialExitValue == 0 -> Status.ACCEPTED
                specialExitValue < 0 -> Status.PRESENTATION_ERROR
                else -> Status.WRONG_ANSWER
            }
        } catch (ex: InterruptedException) {
            throw InterruptedIOException()
        }
    }

    companion object {
        private val log = LoggerFactory.getLogger(SpecialValidator::class.java)
    }
}
