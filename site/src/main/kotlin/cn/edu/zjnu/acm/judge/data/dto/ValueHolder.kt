package cn.edu.zjnu.acm.judge.data.dto

import java.io.Serializable

data class ValueHolder<T : Serializable>(
        var value: T? = null
) : Serializable {
    companion object {
        private const val serialVersionUID = 1L
    }
}
