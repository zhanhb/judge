package cn.edu.zjnu.acm.judge.support

import cn.edu.zjnu.acm.judge.core.Status

/**
 *
 * @author zhanhb
 */
data class RunResult(
        val type: Status? = null,
        val score: Int = 0,
        val compileInfo: String? = null,
        val detail: String? = null,
        val systemInfo: String? = null,
        val time: Long = 0,
        val memory: Long = 0
)
