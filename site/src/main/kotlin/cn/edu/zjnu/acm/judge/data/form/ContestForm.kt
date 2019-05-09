package cn.edu.zjnu.acm.judge.data.form

import java.io.Serializable

data class ContestForm(
        var includeDisabled: Boolean = false,
        var exclude: Array<String?>? = null,
        var include: Array<String?>? = null
) : Serializable {
    companion object {
        private const val serialVersionUID = 1L
    }
}
