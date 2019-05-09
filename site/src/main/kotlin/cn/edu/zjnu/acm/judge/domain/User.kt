package cn.edu.zjnu.acm.judge.domain

import com.fasterxml.jackson.annotation.JsonIgnore
import java.time.Instant
import java.io.Serializable

/**
 * @author zhanhb
 */
data class User(
        var id: String? = null,
        var email: String? = null,
        @JsonIgnore
        var password: String? = null,
        var nick: String? = null,
        var school: String? = null,
        var submit: Long? = null,
        var solved: Long? = null,
        var accesstime: Instant? = null,
        var ip: String? = null,
        var createdTime: Instant? = null,
        var modifiedTime: Instant? = null,
        var disabled: Boolean? = null
) : Serializable {

    val ratio: Double
        @JsonIgnore
        get() {
            try {
                return Math.round(solved!! * 1000.0 / submit!!) / 10.0
            } catch (ex: NullPointerException) {
                return 0.0
            }

        }

    companion object {
        private const val serialVersionUID = 1L
    }
}
