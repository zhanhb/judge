package cn.edu.zjnu.acm.judge.domain

import cn.edu.zjnu.acm.judge.util.SpecialCall
import com.fasterxml.jackson.annotation.JsonIgnore
import java.io.Serializable
import java.time.Instant

data class Contest(
        var id: Long? = null,
        var title: String? = null,
        var description: String? = null,
        var startTime: Instant? = null,
        var endTime: Instant? = null,
        var disabled: Boolean? = null,
        var createdTime: Instant? = null,
        var modifiedTime: Instant? = null,
        var problems: List<Problem>? = null
) : Serializable {

    val isStarted: Boolean
        @JsonIgnore
        @SpecialCall("contests/problems")
        get() {
            val t = startTime
            return t == null || t.isBefore(Instant.now())
        }

    val isEnded: Boolean
        @JsonIgnore
        get() {
            val t = endTime
            return t != null && t.isBefore(Instant.now())
        }

    val isError: Boolean
        @JsonIgnore
        get() {
            val s = startTime
            val e = endTime
            return s != null && e != null && s.isAfter(e)
        }

    companion object {
        private const val serialVersionUID = 1L
    }

}
