package cn.edu.zjnu.acm.judge.domain

import java.io.Serializable
import java.time.Instant

data class Message(
        var id: Long = 0,
        var inDate: Instant? = null,
        var parent: Long? = null,
        var user: String? = null,
        var content: String? = null,
        var title: String? = null,
        var problem: Long? = null,
        var depth: Long = 0,
        var thread: Long = 0,
        var order: Long = 0
) : Serializable {
    companion object {
        private const val serialVersionUID = 1L
    }
}
