package cn.edu.zjnu.acm.judge.data.form

import cn.edu.zjnu.acm.judge.data.excel.Account
import cn.edu.zjnu.acm.judge.mapper.UserMapper
import java.io.Serializable
import java.util.*

/**
 * @author zhanhb
 */
data class AccountImportForm(
        var content: List<Account>? = null,
        var existsPolicy: EnumSet<ExistPolicy> = EnumSet.noneOf(ExistPolicy::class.java)
) : Serializable {

    /**
     * [UserMapper.batchUpdate]
     */
    enum class ExistPolicy {
        ENABLE,
        RESET_PASSWORD,
        RESET_USERINFO
    }
    companion object {
        private const val serialVersionUID = 1L
    }
}
