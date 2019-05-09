package cn.edu.zjnu.acm.judge.data.dto

import java.io.Serializable

data class SubmissionDetailDTO(
        var result: String? = null,
        var score: String? = null,
        var time: String? = null,
        var memory: String? = null
) : Serializable {
    companion object {
        private const val serialVersionUID = 1L
    }
}
