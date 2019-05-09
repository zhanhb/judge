package cn.edu.zjnu.acm.judge.core

import java.io.IOException

/**
 * @author zhanhb
 */
enum class UnsupportedExecutor : Executor {

    INSTANCE;

    @Throws(IOException::class)
    override fun execute(options: Options): ExecuteResult {
        throw UnsupportedOperationException()
    }

    override fun close() {}

}
