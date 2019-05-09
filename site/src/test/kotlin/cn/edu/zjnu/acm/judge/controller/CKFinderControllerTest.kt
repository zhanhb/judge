package cn.edu.zjnu.acm.judge.controller

import cn.edu.zjnu.acm.judge.Application
import cn.edu.zjnu.acm.judge.service.SystemService
import cn.edu.zjnu.acm.judge.util.DeleteHelper
import cn.edu.zjnu.acm.judge.util.PlatformAssuming.assumingNotWindows
import cn.edu.zjnu.acm.judge.util.PlatformAssuming.assumingWindows
import org.hamcrest.Matchers.containsString
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.platform.runner.JUnitPlatform
import org.junit.runner.RunWith
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.web.WebAppConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.MvcResult
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.transaction.annotation.Transactional
import java.io.IOException
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path

@AutoConfigureMockMvc
@RunWith(JUnitPlatform::class)
@SpringBootTest(classes = [Application::class])
@Transactional
@WebAppConfiguration
class CKFinderControllerTest {

    @Autowired
    private val mvc: MockMvc? = null
    @Autowired
    private val systemService: SystemService? = null
    private var pathName = "test.jpg"
    private var dir: Path? = null
    private var path: Path? = null
    private var content: ByteArray? = null

    @Throws(IOException::class)
    fun setUp() {
        dir = Files.createDirectories(systemService!!.uploadDirectory)
        path = dir!!.fileSystem.getPath(dir!!.toString(), pathName)
        content = "Hello! But I'm not a picture!".toByteArray(StandardCharsets.UTF_8)
        val parent = path!!.parent
        if (parent != null) {
            Files.createDirectories(parent)
        }
        Files.write(path, content)
    }

    @Throws(IOException::class)
    fun tearDown() {
        DeleteHelper.delete(path!!)
    }

    /**
     * Test of legacySupport method, of class CKFinderController.
     *
     * [CKFinderController.legacySupport]
     */
    @Test
    @Throws(Exception::class)
    fun testLegacySupport() {
        log.info("legacySupport")
        setUp()
        mvc!!.perform(get("/support/ckfinder.action").param("path", pathName))
                .andExpect(status().isOk)
                .andExpect(content().contentTypeCompatibleWith(MediaType.IMAGE_JPEG))
                .andExpect(content().bytes(content!!))
                .andReturn()
        mvc.perform(get("/support/ckfinder.action?path=$pathName?hash-1"))
                .andExpect(status().isOk)
                .andExpect(content().contentTypeCompatibleWith(MediaType.IMAGE_JPEG))
                .andExpect(content().bytes(content!!))
                .andReturn()
        tearDown()
    }

    @Test
    @Throws(Exception::class)
    fun testGetParent() {
        pathName = "../../hello.txt"
        setUp()
        mvc!!.perform(get("/support/ckfinder.action?path=$pathName"))
                .andExpect(status().isNotFound)
                .andExpect(handler().handlerType(CKFinderController::class.java))
                .andReturn()
        tearDown()
    }

    @Test
    @Throws(Exception::class)
    fun testSlashRoot() {
        for (s in arrayOf("/aj.txt", "//ba.txt", "///cc.txt")) {
            pathName = s
            setUp()
            assertTrue(path!!.startsWith(dir))
            mvc!!.perform(get("/support/ckfinder.action?path=$pathName"))
                    .andExpect(status().isOk)
                    .andExpect(handler().handlerType(CKFinderController::class.java))
                    .andReturn()
            tearDown()
        }
    }

    /**
     * Test of attachment method, of class CKFinderController.
     *
     * [CKFinderController.attachment]
     */
    @Test
    @Throws(Exception::class)
    fun testAttachment() {
        log.info("attachment")
        setUp()
        mvc!!.perform(get("/userfiles/$pathName"))
                .andExpect(status().isOk)
                .andExpect(content().contentTypeCompatibleWith(MediaType.IMAGE_JPEG))
                .andExpect(content().bytes(content!!))
                .andReturn()
        // query handled by framework
        mvc.perform(get("/userfiles/$pathName?hash=1"))
                .andExpect(status().isOk)
                .andExpect(content().contentTypeCompatibleWith(MediaType.IMAGE_JPEG))
                .andExpect(content().bytes(content!!))
                .andReturn()
        for (uri in arrayOf("/userfiles/images", "/userfiles/images/", "/userfiles/images/sample.png", "/userfiles/images/folder/sample.png", "/userfiles/images/f1/f2/sample.png")) {
            mvc.perform(get(uri))
                    .andExpect(handler().handlerType(CKFinderController::class.java))
        }
        tearDown()
    }

    @Test
    @Throws(Exception::class)
    fun testDotDirectory() {
        pathName = ".git/head"
        setUp()
        mvc!!.perform(get("/userfiles/$pathName"))
                .andExpect(status().isNotFound)
                .andExpect(handler().handlerType(CKFinderController::class.java))
                .andReturn()
        tearDown()
    }

    @Test
    @Throws(Exception::class)
    fun testIaeOnWindows() {
        assumingWindows()
        pathName = "::::::"
        assertThrows(IllegalArgumentException::class.java) { setUp() }
        mvc!!.perform(get("/userfiles/$pathName"))
                .andExpect(status().isNotFound)
                .andExpect(handler().handlerType(CKFinderController::class.java))
                .andReturn()
    }

    @Test
    @Throws(Exception::class)
    fun testColonOnUnix() {
        assumingNotWindows()
        pathName = "::::::"
        setUp()
        mvc!!.perform(get("/userfiles/$pathName"))
                .andExpect(status().isOk)
                .andExpect(handler().handlerType(CKFinderController::class.java))
                .andReturn()
        tearDown()
    }

    @Throws(Exception::class)
    private fun testConfigJsBase(requestLang: String?, expect: String): MvcResult {
        val builder = get("/webjars/ckfinder/2.6.2.1/config.js")
        if (requestLang != null) {
            builder.param("lang", requestLang)
        }
        return mvc!!.perform(builder)
                .andExpect(status().isOk)
                .andExpect(content().contentTypeCompatibleWith("application/javascript"))
                .andExpect(content().string(containsString(expect)))
                .andReturn()
    }

    /**
     * Test of configJs method, of class CKFinderController.
     *
     * [CKFinderController.configJs]
     */
    @Test
    @Throws(Exception::class)
    fun testConfigJs() {
        log.info("configJs")
        var result = testConfigJsBase("pt-BR", "pt-br")
        result = testConfigJsBase("zh", "zh-cn")
        result = testConfigJsBase("zh-TW-x-lvariant-Hant", "zh-tw")
        result = testConfigJsBase(null, "en")
        result = testConfigJsBase("", "en")
    }

    companion object {
        private val log = LoggerFactory.getLogger(CKFinderControllerTest::class.java)
    }
}
