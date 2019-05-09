//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package cn.edu.zjnu.acm.judge.config

import cn.edu.zjnu.acm.judge.service.SystemService
import com.github.zhanhb.ckfinder.connector.api.BasePathBuilder
import org.springframework.beans.BeansException
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.stereotype.Service
import java.nio.file.Path
import javax.servlet.ServletContext

@Service("basePathBuilder")
class BasePathBuilderImpl(private val systemService: SystemService) : BasePathBuilder, ApplicationContextAware {
    private var url: String? = null

    override fun getBasePath(): Path {
        return this.systemService.uploadDirectory
    }

    override fun getBaseUrl(): String? {
        return this.url
    }

    @Throws(BeansException::class)
    override fun setApplicationContext(applicationContext: ApplicationContext) {
        this.url = applicationContext.getBean(ServletContext::class.java).contextPath + "/userfiles/"
    }
}
