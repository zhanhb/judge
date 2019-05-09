package cn.edu.zjnu.acm.judge.domain

import java.io.Serializable
import java.time.Instant

data class DomainLocale(
        var id: String? = null,
        var name: String? = null,
        var disabled: Boolean? = null,
        var createdTime: Instant? = null,
        var modifiedTime: Instant? = null
) : Serializable {
    companion object {
        private const val serialVersionUID = 1L
    }
}
