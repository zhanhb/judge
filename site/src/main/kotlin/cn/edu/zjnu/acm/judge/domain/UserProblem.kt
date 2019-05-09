package cn.edu.zjnu.acm.judge.domain

import java.io.Serializable

data class UserProblem(
        var user: String? = null,
        var problem: Long = 0,
        var submit: Long = 0,
        var accepted: Long = 0
) : Serializable {
    companion object {
        private const val serialVersionUID = 1L
    }
}
