package cn.edu.zjnu.acm.judge.sandbox.win32

import cn.edu.zjnu.acm.judge.core.Status
import com.google.common.base.Preconditions
import jnc.foreign.byref.IntByReference
import jnc.platform.win32.*
import jnc.platform.win32.WinBase.WAIT_ABANDONED
import jnc.platform.win32.WinBase.WAIT_FAILED
import jnc.platform.win32.WinError.WAIT_TIMEOUT
import java.time.Instant
import java.util.concurrent.atomic.AtomicReference

class JudgeProcess internal constructor(private val /*HANDLE*/ hProcess: Long/*HANDLE*/) {
    private val status = AtomicReference<Status>()

    val peakMemory: Long
        get() {
            val ppsmemCounters = PROCESS_MEMORY_COUNTERS()
            Kernel32Util.assertTrue(Psapi.INSTANCE.GetProcessMemoryInfo(hProcess, ppsmemCounters, ppsmemCounters.size()))
            return ppsmemCounters.peakWorkingSetSize
        }

    val startTime: Instant
        get() {
            val ftCreateTime = FILETIME()
            val temp = FILETIME()
            Kernel32Util.assertTrue(Kernel32.INSTANCE.GetProcessTimes(hProcess, ftCreateTime, temp, temp, temp))
            return ftCreateTime.toInstant()
        }

    val time: Long
        get() {
            val tmp = FILETIME()
            val ftKernelTime = FILETIME()
            val ftUserTime = FILETIME()
            Kernel32Util.assertTrue(Kernel32.INSTANCE.GetProcessTimes(hProcess, tmp, tmp, ftKernelTime, ftUserTime))
            return java.lang.Long.divideUnsigned(ftUserTime.longValue() + ftKernelTime.longValue(), 10000)
        }

    val exitCode: Int
        get() {
            val dwExitCode = IntByReference()
            Kernel32Util.assertTrue(Kernel32.INSTANCE.GetExitCodeProcess(hProcess, dwExitCode))
            return dwExitCode.value
        }

    fun terminate(errorCode: Status) {
        if (status.compareAndSet(null, errorCode)) {
            try {
                Handle.validateHandle(hProcess)
            } catch (ex: IllegalArgumentException) {
                return
            }

            // don't check the return value, maybe the process has already exited.
            Kernel32.INSTANCE.TerminateProcess(hProcess, 1)
        }
    }

    private fun join0(millis: Int): Boolean {
        return when (Kernel32.INSTANCE.WaitForSingleObject(hProcess, millis)) {
            WAIT_ABANDONED -> throw IllegalStateException()
            WAIT_FAILED -> throw Win32Exception(Kernel32Util.lastError)
            WAIT_TIMEOUT -> false
            else -> true
        }
    }

    fun join(millis: Long): Boolean {
        Preconditions.checkArgument(millis >= 0)
        return join0(Math.min(millis, 0xFFFFFFFEL).toInt())
    }

    fun getStatus(): Status {
        return status.get()
    }

}
