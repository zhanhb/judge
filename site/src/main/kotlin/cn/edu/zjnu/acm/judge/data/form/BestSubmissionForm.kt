package cn.edu.zjnu.acm.judge.data.form

import java.io.Serializable

data class BestSubmissionForm(
        var contestId: Long? = null,
        var problemId: Long = 0
) : Serializable {
    companion object {
        private const val serialVersionUID = 1L
    }
}
