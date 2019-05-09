package cn.edu.zjnu.acm.judge.data.dto

import java.io.Serializable

data class ScoreCount(
        var score: Int = 0,
        var count: Long = 0
) : Serializable {
    companion object {
        private const val serialVersionUID = 1L
    }
}
