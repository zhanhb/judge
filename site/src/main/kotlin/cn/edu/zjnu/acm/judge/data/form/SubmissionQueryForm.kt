package cn.edu.zjnu.acm.judge.data.form

import java.io.Serializable

data class SubmissionQueryForm(
        var user: String? = null,
        var problem: Long? = null,
        var contest: Long? = null,
        var language: Int? = null,
        var size: Int = 20,
        var top: Long? = null,
        var bottom: Long? = null,
        var score: Int? = null
) : Serializable {
    companion object {
        private const val serialVersionUID = 1L
    }
}
