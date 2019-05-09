package cn.edu.zjnu.acm.judge.domain

import java.time.Instant
import java.io.Serializable

data class Submission(
        var id: Long = 0,
        var problem: Long = 0,
        var user: String? = null,
        var contest: Long? = null,
        var time: Long = 0,
        var memory: Long = 0,
        var score: Int = 0,
        var language: Int = 0,
        var ip: String? = null,
        var sourceLength: Int = 0,
        var inDate: Instant? = null,
        // TODO it seem on is of no use
        var on: Int? = null,

        // for best submission
        var count: Long? = null
) : Serializable {
    companion object {
        private const val serialVersionUID = 1L
    }
}
