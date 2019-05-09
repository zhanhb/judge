package cn.edu.zjnu.acm.judge.data.dto

import java.io.Serializable

data class Standing(
        var user: String? = null,
        var problem: Long = 0,
        var time: Long? = null,
        var penalty: Long = 0
) : Serializable {
    companion object {
        private const val serialVersionUID = 1L
    }
}
