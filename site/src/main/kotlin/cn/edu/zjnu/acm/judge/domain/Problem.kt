package cn.edu.zjnu.acm.judge.domain

import com.fasterxml.jackson.annotation.JsonIgnore
import java.io.Serializable
import java.time.Instant

data class Problem(
        var id: Long? = null,
        var title: String? = null,
        var description: String? = null,
        var input: String? = null,
        var output: String? = null,
        var sampleInput: String? = null,
        var sampleOutput: String? = null,
        var hint: String? = null,
        var source: String? = null,
        var timeLimit: Long? = null,
        var memoryLimit: Long? = null,

        var accepted: Long? = null,
        var submit: Long? = null,
        var submitUser: Long? = null,
        var solved: Long? = null,
        var inDate: Instant? = null,
        var disabled: Boolean? = null,
        var createdTime: Instant? = null,
        var modifiedTime: Instant? = null,

        var origin: Long? = null,
        var contests: LongArray? = null

) : Serializable {

    // 0 not submitted, 1 accepted, 2 wrong answer
    val status: Int? = null

    val ratio: Int
        @JsonIgnore
        get() {
            val s = submit
            val a = accepted
            return if (s == null || a == null || s == 0L) 0 else Math.round(a * 100.0 / s).toInt()
        }

    val difficulty: Int
        @JsonIgnore
        get() {
            val s = submit
            val a = accepted
            return if (s == null || a == null || s == 0L) 0 else Math.round((s - a) * 100.0 / s).toInt()
        }

    companion object {
        private const val serialVersionUID = 1L
    }
}
