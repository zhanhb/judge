package cn.edu.zjnu.acm.judge.data.form

import java.io.Serializable

data class ProblemForm(
        var sstr: String? = null,
        var disabled: Boolean? = null
) : Serializable {
    companion object {
        private const val serialVersionUID = 1L
    }
}
