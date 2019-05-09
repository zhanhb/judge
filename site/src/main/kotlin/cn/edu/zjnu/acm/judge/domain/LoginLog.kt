package cn.edu.zjnu.acm.judge.domain

import java.io.Serializable

data class LoginLog(
        var id: Long = 0,
        var user: String? = null,
        var password: String? = null,
        var type: String? = null,
        var ip: String? = null,
        var success: Boolean = false
) : Serializable {
    companion object {
        private const val serialVersionUID = 1L
    }
}
