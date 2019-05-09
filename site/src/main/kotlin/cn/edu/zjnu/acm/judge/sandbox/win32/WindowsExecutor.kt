/*
 * Copyright 2017 ZJNU ACM.
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

import jnc.platform.win32.*
import jnc.platform.win32.FileApi.CREATE_ALWAYS
import jnc.platform.win32.FileApi.OPEN_ALWAYS
import jnc.platform.win32.FileApi.OPEN_EXISTING
import jnc.platform.win32.WinBase.CREATE_BREAKAWAY_FROM_JOB
import jnc.platform.win32.WinBase.CREATE_NEW_PROCESS_GROUP
import jnc.platform.win32.WinBase.CREATE_NO_WINDOW
import jnc.platform.win32.WinBase.CREATE_SUSPENDED
import jnc.platform.win32.WinBase.CREATE_UNICODE_ENVIRONMENT
import jnc.platform.win32.WinBase.DETACHED_PROCESS
import jnc.platform.win32.WinBase.FILE_FLAG_DELETE_ON_CLOSE
import jnc.platform.win32.WinBase.FILE_FLAG_WRITE_THROUGH
import jnc.platform.win32.WinBase.HANDLE_FLAG_INHERIT
import jnc.platform.win32.WinBase.HIGH_PRIORITY_CLASS
import jnc.platform.win32.WinBase.STARTF_FORCEOFFFEEDBACK
import jnc.platform.win32.WinBase.STARTF_USESTDHANDLES
import jnc.platform.win32.WinNT.FILE_ATTRIBUTE_NORMAL
import jnc.platform.win32.WinNT.FILE_SHARE_READ
import jnc.platform.win32.WinNT.FILE_SHARE_WRITE
import jnc.platform.win32.WinNT.GENERIC_READ
import jnc.platform.win32.WinNT.GENERIC_WRITE
import cn.edu.zjnu.acm.judge.core.Constants.TERMINATE_TIMEOUT
import cn.edu.zjnu.acm.judge.core.Constants.UPDATE_TIME_THRESHOLD
import cn.edu.zjnu.acm.judge.core.ExecuteResult
import cn.edu.zjnu.acm.judge.core.Executor
import cn.edu.zjnu.acm.judge.core.Options
import cn.edu.zjnu.acm.judge.core.Status
import java.io.IOException
import java.nio.channels.FileChannel
import java.nio.file.Path
import java.time.Instant
import java.time.temporal.ChronoUnit

/**
 *
 * @author zhanhb
 */
class WindowsExecutor : Executor {

    private val DESKTOP = WString.toNative("Winsta0\\default")
    private val EMPTY_ENV = WString.toNative("\u0000")
    private val hToken: Handle

    init {
        hToken = Handle(Sandbox.INSTANCE.createRestrictedToken(
                TokenLevel.USER_LIMITED,
                IntegrityLevel.INTEGRITY_LEVEL_LOW,
                TokenType.PRIMARY,
                true))
    }

    private fun fileOpen(path: Path, flags: Int): Handle {
        val access = if (flags and Executor.O_WRONLY !== 0)
            GENERIC_WRITE
        else if (flags and Executor.O_RDWR !== 0)
            GENERIC_READ or GENERIC_WRITE
        else
            GENERIC_READ
        val sharing = FILE_SHARE_READ or FILE_SHARE_WRITE
        /* Note: O_TRUNC overrides O_CREAT */
        val disposition = if (flags and Executor.O_TRUNC !== 0)
            CREATE_ALWAYS
        else if (flags and Executor.O_CREAT !== 0)
            OPEN_ALWAYS
        else
            OPEN_EXISTING
        val maybeWriteThrough = if (flags and (Executor.O_SYNC or Executor.O_DSYNC) !== 0)
            FILE_FLAG_WRITE_THROUGH
        else
            FILE_ATTRIBUTE_NORMAL
        val maybeDeleteOnClose = if (flags and Executor.O_TEMPORARY !== 0)
            FILE_FLAG_DELETE_ON_CLOSE
        else
            FILE_ATTRIBUTE_NORMAL

        val flagsAndAttributes = maybeWriteThrough or maybeDeleteOnClose
        val h = Kernel32.INSTANCE.CreateFileW(
                WString.toNative(path.toString())!!, /* Wide char path name */
                access, /* Read and/or write permission */
                sharing, null, /* Security attributes */
                disposition, /* creation disposition */
                flagsAndAttributes, /* flags and attributes */
                0 /*NULL*/)/* File sharing flags */
        return Handle(h)
    }

    @Throws(IOException::class)
    override fun execute(options: Options): ExecuteResult {
        val inputFile = options.inputFile
        val outputPath = options.outputFile
        val redirectErrorStream = options.isRedirectErrorStream
        val errorPath = options.errFile
        val workDirectory = options.workDirectory
        val command = options.command

        val timeLimit = options.timeLimit
        val memoryLimit = options.memoryLimit
        val outputLimit = options.outputLimit

        // TODO
        var pi = PROCESS_INFORMATION()

        fileOpen(inputFile!!, Executor.O_RDONLY).use { hIn ->
            fileOpen(outputPath!!, Executor.O_WRONLY or Executor.O_CREAT or Executor.O_TRUNC).use { hOut ->
                (if (redirectErrorStream) hOut else fileOpen(errorPath!!, Executor.O_WRONLY or Executor.O_CREAT or Executor.O_TRUNC)).use { hErr ->
                    pi = createProcess(command, hIn.value, hOut.value, hErr.value, redirectErrorStream, workDirectory)
                }
            }
        }

        Job().use { job ->
            Handle(pi.process).use { hProcess ->
                Handle(pi.thread).use { hThread ->
                    val judgeProcess = JudgeProcess(hProcess.value)
                    try {
                        FileChannel.open(outputPath).use { cOut ->
                            (if (redirectErrorStream) cOut else FileChannel.open(errorPath)).use { cErr ->
                                job.init()
                                job.assignProcess(hProcess.value)

                                val dwCount = Kernel32.INSTANCE.ResumeThread(hThread.value)
                                Kernel32Util.assertTrue(dwCount != -1)
                                hThread.close()

                                val startTime = judgeProcess.startTime
                                while (true) {
                                    val memory = judgeProcess.peakMemory
                                    if (memory > memoryLimit) {
                                        judgeProcess.terminate(Status.MEMORY_LIMIT_EXCEED)
                                        judgeProcess.join(TERMINATE_TIMEOUT.toLong())
                                        break
                                    }
                                    val time = ChronoUnit.MILLIS.between(startTime, Instant.now()) - 5000 // extra 5 seconds
                                    if (time > timeLimit || judgeProcess.time > timeLimit) {
                                        judgeProcess.terminate(Status.TIME_LIMIT_EXCEED)
                                        judgeProcess.join(TERMINATE_TIMEOUT.toLong())
                                        break
                                    }
                                    var dwWaitTime = timeLimit - time
                                    if (dwWaitTime > UPDATE_TIME_THRESHOLD) {
                                        dwWaitTime = UPDATE_TIME_THRESHOLD.toLong()
                                    }
                                    if (judgeProcess.join(dwWaitTime)) {
                                        break
                                    }
                                    if (checkOle(cOut, cErr, redirectErrorStream, outputLimit)) {
                                        judgeProcess.terminate(Status.OUTPUT_LIMIT_EXCEED)
                                        judgeProcess.join(TERMINATE_TIMEOUT.toLong())
                                        break
                                    }
                                }
                                if (checkOle(cOut, cErr, redirectErrorStream, outputLimit)) {
                                    judgeProcess.terminate(Status.OUTPUT_LIMIT_EXCEED)
                                }
                            }
                        }
                    } finally {
                        judgeProcess.terminate(Status.ACCEPTED)
                    }
                    judgeProcess.join(java.lang.Long.MAX_VALUE)
                    var status = judgeProcess.getStatus()
                    val exitCode = judgeProcess.exitCode
                    if (status === Status.ACCEPTED && exitCode != 0) {
                        status = Status.RUNTIME_ERROR
                    }
                    var time = judgeProcess.time
                    if (status === Status.TIME_LIMIT_EXCEED) {
                        time = ((time - timeLimit - 1) % 200 + 200) % 200 + 1 + timeLimit
                    }
                    return ExecuteResult(time = time, memory = judgeProcess.peakMemory, code = status, exitCode = exitCode)
                }
            }
        }
    }

    private fun setInheritable(handle: Long) {
        Kernel32Util.assertTrue(Kernel32.INSTANCE.SetHandleInformation(handle, HANDLE_FLAG_INHERIT, HANDLE_FLAG_INHERIT))
    }

    private fun createProcess(lpCommandLine: String,
            /*HANDLE*/ hIn: Long, /*HANDLE*/ hOut: Long, /*HANDLE*/ hErr: Long,
                              redirectErrorStream: Boolean, lpCurrentDirectory: Path?): PROCESS_INFORMATION {
        val lpApplicationName: String? = null
        val lpProcessAttributes: SECURITY_ATTRIBUTES? = null
        val lpThreadAttributes: SECURITY_ATTRIBUTES? = null
        val dwCreationFlags = (CREATE_SUSPENDED
                or DETACHED_PROCESS
                or HIGH_PRIORITY_CLASS
                or CREATE_NEW_PROCESS_GROUP
                or CREATE_UNICODE_ENVIRONMENT
                or CREATE_BREAKAWAY_FROM_JOB
                or CREATE_NO_WINDOW)
        val lpStartupInfo = STARTUPINFO()
        lpStartupInfo.setCb(lpStartupInfo.size())
        lpStartupInfo.desktop = DESKTOP!!.address()
        val lpProcessInformation = PROCESS_INFORMATION()

        // without cursor feed back
        lpStartupInfo.flags = STARTF_USESTDHANDLES or STARTF_FORCEOFFFEEDBACK
        lpStartupInfo.stdInput = hIn
        lpStartupInfo.stdOutput = hOut
        lpStartupInfo.stdError = hErr

        setInheritable(hIn)
        setInheritable(hOut)
        if (!redirectErrorStream) {
            setInheritable(hErr)
        }

        ProcessCreationHelper.execute {
            Kernel32Util.assertTrue(Advapi32.INSTANCE.CreateProcessAsUserW(
                    hToken.value,
                    WString.toNative(lpApplicationName), // executable name
                    WString.toNative(lpCommandLine), // command line
                    lpProcessAttributes, // process security attribute
                    lpThreadAttributes, // thread security attribute
                    true, // inherits system handles
                    dwCreationFlags, // selected based on exe type
                    EMPTY_ENV,
                    WString.toNative(lpCurrentDirectory?.toString()),
                    lpStartupInfo,
                    lpProcessInformation))
        }
        return lpProcessInformation
    }

    @Throws(IOException::class)
    private fun checkOle(outputPath: FileChannel, errorPath: FileChannel,
                         redirectErrorStream: Boolean, outputLimit: Long): Boolean {
        return outputPath.size() > outputLimit || !redirectErrorStream && errorPath.size() > outputLimit
    }

    override fun close() {
        hToken.close()
    }

}
