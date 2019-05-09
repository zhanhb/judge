package cn.edu.zjnu.acm.judge.core

import java.io.Closeable
import java.io.IOException

/**
 * @author zhanhb
 */
interface Executor : Closeable {

    @Throws(IOException::class)
    fun execute(options: Options): ExecuteResult

    override fun close()

    companion object {

        val _O_RDONLY = 0
        val _O_WRONLY = 1
        val _O_RDWR = 2
        val _O_TEMPORARY = 0x0040
        val _O_CREAT = 0x0100
        /* Create the file if it does not exist. */
        val _O_TRUNC = 0x0200
        /* Truncate the file if it does exist. */
        val O_RDONLY = _O_RDONLY
        val O_WRONLY = _O_WRONLY
        val O_RDWR = _O_RDWR
        val O_CREAT = _O_CREAT
        val O_TRUNC = _O_TRUNC
        val O_TEMPORARY = _O_TEMPORARY
        val O_SYNC = 0x0800
        val O_DSYNC = 0x2000
    }

}
