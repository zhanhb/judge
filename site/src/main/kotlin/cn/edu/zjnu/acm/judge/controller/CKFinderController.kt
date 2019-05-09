/*
 * Copyright 2014-2019 zhanhb.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.edu.zjnu.acm.judge.controller

import com.github.zhanhb.ckfinder.connector.api.BasePathBuilder
import com.github.zhanhb.ckfinder.connector.utils.FileUtils
import com.github.zhanhb.ckfinder.download.ContentDispositionStrategy
import com.github.zhanhb.ckfinder.download.PathPartial
import org.slf4j.LoggerFactory
import org.springframework.context.MessageSource
import org.springframework.stereotype.Controller
import org.springframework.util.AntPathMatcher
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.servlet.HandlerMapping
import java.io.IOException
import java.io.PrintWriter
import java.io.StringWriter
import java.nio.file.Path
import java.util.*
import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Controller
class CKFinderController(
        private val basePathBuilder: BasePathBuilder,
        private val messageSource: MessageSource
) {
    private val matcher = AntPathMatcher()
    private val viewer = PathPartial.builder()
            .contentDisposition(ContentDispositionStrategy.inline())
            .build()
    private val pathPartial = PathPartial.builder().build()

    private fun toPath(path: String): Path? {
        log.info(path)
        // ignore all dot directories, such as a/./a, a/../b, .git/info
        val st = StringTokenizer(path, "/\\")
        while (st.hasMoreTokens()) {
            if (st.nextToken().startsWith(".")) {
                return null
            }
        }
        try {
            return FileUtils.resolve(basePathBuilder.basePath, path)
        } catch (ex: IllegalArgumentException) {
            return null
        }

    }

    @Deprecated("")
    @GetMapping("/support/ckfinder.action")
    @Throws(IOException::class, ServletException::class)
    fun legacySupport(request: HttpServletRequest, response: HttpServletResponse,
                      @RequestParam("path") path: String) {
        val indexOf = path.indexOf('?')
        viewer.service(request, response, toPath(if (indexOf > 0) path.substring(0, indexOf) else path))
    }

    @GetMapping("/userfiles/{first}/**")
    @Throws(IOException::class, ServletException::class)
    fun attachment(request: HttpServletRequest, response: HttpServletResponse,
                   @PathVariable("first") first: String) {
        val uri = request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE).toString()
        val pattern = request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE).toString()
        val rest = matcher.extractPathWithinPattern(pattern, uri)
        @Suppress("UselessCallOnNotNull") val path = if (rest.isNullOrEmpty()) first else "$first/$rest"
        pathPartial.service(request, response, toPath(path))
    }

    @ResponseBody
    @GetMapping(value = ["webjars/ckfinder/2.6.2.1/config"], produces = ["application/javascript", "text/javascript"])
    fun configJs(locale: Locale?): String {
        val indent = "    "
        val sw = StringWriter()
        PrintWriter(sw).use { pw ->
            pw.println("CKFinder.customConfig = function(config) {")
            pw.println("$indent// Define changes to default configuration here.")
            pw.println("$indent// For the list of available options, check:")
            pw.println("$indent// http://docs.cksource.com/ckfinder_2.x_api/symbols/CKFinder.config.html")
            pw.println()
            pw.println("$indent// Sample configuration options:")
            pw.println("$indent// config.uiColor = '#BDE31E';")
            if (locale != null) {
                val fallback = locale.toLanguageTag().toLowerCase(Locale.US)
                val lang = messageSource.getMessage("ckfinder.lang", arrayOfNulls<Any>(0), fallback, locale)
                pw.println(indent + "config.language = '" + lang + "';")
            }
            pw.println(indent + "config.removePlugins = 'help';")
            pw.println("};")
        }
        return sw.toString()
    }

    companion object {
        private val log = LoggerFactory.getLogger(CKFinderController::class.java)
    }
}
