package cn.edu.zjnu.acm.judge.data.form

import java.io.Serializable

data class UserPasswordForm(
        var password: String? = null
) : Serializable {
    companion object {
        private const val serialVersionUID = 1L
    }
}
