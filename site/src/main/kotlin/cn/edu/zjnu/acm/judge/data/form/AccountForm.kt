package cn.edu.zjnu.acm.judge.data.form

import java.io.Serializable

data class AccountForm(
         var userId: String? = null,
         var nick: String? = null,
         var query: String? = null,
         var disabled: Boolean? = null
) : Serializable {
    companion object {
        private const val serialVersionUID = 1L
    }
}
