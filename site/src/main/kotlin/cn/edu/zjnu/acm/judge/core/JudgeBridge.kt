package cn.edu.zjnu.acm.judge.core

import cn.edu.zjnu.acm.judge.sandbox.win32.WindowsExecutor
import jnc.foreign.Platform
import org.slf4j.LoggerFactory
import java.io.Closeable
import java.io.IOException
import java.util.*

/**
 *
 * @author zhanhb
 */
class JudgeBridge : Closeable {

    private val executor: Executor

    init {
        if (Platform.getNativePlatform().os.isWindows) {
            executor = WindowsExecutor()
        } else {
            executor = UnsupportedExecutor.INSTANCE
        }
    }

    @Throws(IOException::class)
    fun judge(optionses: List<Options>, stopOnError: Boolean, validator: Validator): Array<ExecuteResult> {
        // the first case takes much more time than other cases.
        executor.execute(optionses[0])
        val list = ArrayList<ExecuteResult>(optionses.size)
        for (options in optionses) {
            log.debug("prepare execute {}", options)
            var result = executor.execute(options)
            var success = result.isSuccess
            if (success) {
                result = validator.validate(options, result)
                success = result.isSuccess
            }
            log.info("result:{}", result)
            list.add(result)
            if (stopOnError && !success) {
                break
            }
        }
        return list.toTypedArray()
    }

    override fun close() {
        executor.close()
    }

    companion object {
        private val log = LoggerFactory.getLogger(JudgeBridge::class.java)
    }
}
