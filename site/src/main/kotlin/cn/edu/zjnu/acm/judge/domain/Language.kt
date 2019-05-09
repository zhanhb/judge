package cn.edu.zjnu.acm.judge.domain

import java.io.Serializable
import java.time.Instant

data class Language(
        var id: Int = 0,
        var name: String? = null,
        var sourceExtension: String? = null,
        var compileCommand: String? = null,
        var executeCommand: String? = null,
        var executableExtension: String? = null,
        var timeFactor: Long = 0,
        var extTime: Long = 0,
        var extMemory: Long = 0,
        var description: String? = null,
        var createdTime: Instant? = null,
        var modifiedTime: Instant? = null,
        var disabled: Boolean? = null
) : Serializable {
    companion object {
        private const val serialVersionUID = 1L
    }
}
