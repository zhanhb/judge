package cn.edu.zjnu.acm.judge.support

import cn.edu.zjnu.acm.judge.domain.Language
import java.io.Serializable

/**
 * @author zhanhb
 */
data class RunRecord(
        val language: Language? = null,
        val timeLimit: Long = 0,
        val memoryLimit: Long = 0,
        val source: String? = null
) : Serializable {
    companion object {
        private const val serialVersionUID = 1L
    }
}
