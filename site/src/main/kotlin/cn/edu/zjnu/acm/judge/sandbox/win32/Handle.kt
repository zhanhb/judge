package cn.edu.zjnu.acm.judge.sandbox.win32

import com.google.common.base.Preconditions
import java.io.Closeable
import java.util.concurrent.atomic.AtomicBoolean
import jnc.foreign.Platform
import jnc.platform.win32.Kernel32;
import jnc.platform.win32.Kernel32Util;

/**
 * @author zhanhb
 */
class Handle(/*HANDLE*/ val value: Long/*HANDLE*/) : Closeable {
    private val closed = AtomicBoolean()

    init {
        validateHandle(value)
    }

    override fun close() {
        if (closed.compareAndSet(false, true)) {
            close(value)
        }
    }

    override fun toString(): String {
        return java.lang.Long.toHexString(value)
    }

    companion object {

        private val INVALID_HANDLE_VALUE: Long

        init {
            when (Platform.getNativePlatform().arch.pointerSize()) {
                32 -> INVALID_HANDLE_VALUE = 0xFFFFFFFFL
                64 -> INVALID_HANDLE_VALUE = -1
                else -> throw AssertionError()
            }
        }

        fun validateHandle(/*HANDLE*/ handle: Long) {
            Preconditions.checkArgument(handle != 0L && handle != INVALID_HANDLE_VALUE, "invalid handle value")
        }

        fun close(/*HANDLE*/ handle: Long) {
            try {
                validateHandle(handle)
            } catch (ex: IllegalArgumentException) {
                return
            }

            Kernel32Util.assertTrue(Kernel32.INSTANCE.CloseHandle(handle))
        }
    }

}
