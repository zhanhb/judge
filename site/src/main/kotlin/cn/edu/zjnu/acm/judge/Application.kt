package cn.edu.zjnu.acm.judge

import com.google.common.base.Strings
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.boot.web.context.WebServerApplicationContext
import org.springframework.cache.annotation.EnableCaching

/**
 * @author zhanhb
 */
@EnableCaching
@SpringBootApplication
class Application

private val log = LoggerFactory.getLogger(Application::javaClass.javaClass)

fun main(args: Array<String>) {
    val context = runApplication<Application>(*args)
    if (log.isInfoEnabled && context is WebServerApplicationContext) {
        val port = context.webServer.port
        val contextPath = context.getApplicationName()
        val dashes = Strings.repeat("-", 72)
        log.info("Access URLs:\n{}\n\tLocal:\t\thttp://localhost:{}{}\n{}", dashes, port, contextPath, dashes)
    }

}
