package cn.edu.zjnu.acm.judge.data.excel

import cn.edu.zjnu.acm.judge.util.excel.Excel
import java.io.Serializable

data class Account(
        @Excel(name = "id", order = 1)
        var id: String? = null,
        @Excel(name = "password", order = 2)
        var password: String? = null,
        @Excel(name = "nick", order = 3)
        var nick: String? = null,
        @Excel(name = "school", order = 4)
        var school: String? = null,
        @Excel(name = "email", order = 5)
        var email: String? = null,
        var exists: Boolean? = null
) : Serializable {
    companion object {
        private const val serialVersionUID = 1L
    }
}
