package cn.edu.zjnu.acm.judge.domain

import java.time.Instant
import java.io.Serializable

data class Mail(
        var id: Long = 0,
        var from: String? = null,
        var to: String? = null,
        var inDate: Instant? = null,
        var title: String? = null,
        var content: String? = null,
        var newMail: Boolean = false
) : Serializable {
    companion object {
        private const val serialVersionUID = 1L
    }
}
