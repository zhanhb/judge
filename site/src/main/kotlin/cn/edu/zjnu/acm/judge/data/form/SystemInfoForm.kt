package cn.edu.zjnu.acm.judge.data.form

import org.unbescape.html.HtmlEscape
import java.io.Serializable

data class SystemInfoForm(
        var info: String? = null,
        var pureText: Boolean = false
) : Serializable {
    override fun toString(): String {
        val info = info
        return if (pureText && !info.isNullOrBlank()) HtmlEscape.escapeHtml4Xml(info) else info.orEmpty()
    }

    companion object {
        private const val serialVersionUID = 1L
    }
}
