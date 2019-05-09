package cn.edu.zjnu.acm.judge.core

/**
 * @author zhanhb
 */
data class ExecuteResult(
        val time: Long = 0,
        val memory: Long = 0,
        val code: Status,
        val exitCode: Int = 0,
        val message: String? = null
) {
    val isSuccess: Boolean
        get() = code === Status.ACCEPTED

}
