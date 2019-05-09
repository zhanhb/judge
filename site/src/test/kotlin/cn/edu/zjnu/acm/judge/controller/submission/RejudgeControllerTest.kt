package cn.edu.zjnu.acm.judge.controller.submission

import cn.edu.zjnu.acm.judge.Application
import cn.edu.zjnu.acm.judge.domain.Submission
import cn.edu.zjnu.acm.judge.service.*
import cn.edu.zjnu.acm.judge.util.CopyHelper
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assumptions.assumeTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.platform.runner.JUnitPlatform
import org.junit.runner.RunWith
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.core.env.Environment
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.context.web.WebAppConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import java.io.IOException
import java.net.URISyntaxException
import java.nio.file.Paths
import java.nio.file.StandardCopyOption
import java.util.*

@AutoConfigureMockMvc(addFilters = false)
@RunWith(JUnitPlatform::class)
@SpringBootTest(classes = [Application::class])
@WebAppConfiguration
@WithMockUser(roles = ["ADMIN"])
class RejudgeControllerTest {

    @Autowired
    private val mvc: MockMvc? = null
    @Autowired
    private val mockDataService: MockDataService? = null
    @Autowired
    private val environment: Environment? = null
    @Autowired
    private val submissionService: SubmissionService? = null
    @Autowired
    private val systemService: SystemService? = null
    private var submission: Submission? = null
    @Autowired
    private val accountService: AccountService? = null
    @Autowired
    private val deleteService: DeleteService? = null

    @BeforeEach
    @Throws(IOException::class, URISyntaxException::class)
    fun setUp() {
        submission = mockDataService!!.submission(false)
        val dataDir = systemService!!.getDataDirectory(submission!!.problem)
        CopyHelper.copy(Paths.get(RejudgeControllerTest::class.java.getResource("/sample/data").toURI()), dataDir, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.COPY_ATTRIBUTES)
    }

    @AfterEach
    @Throws(IOException::class)
    fun tearDown() {
        if (submission != null) {
            deleteService!!.delete(systemService!!.getDataDirectory(submission!!.problem))
            submissionService!!.delete(submission!!.id)
            accountService!!.delete(submission!!.user!!)
        }
    }

    /**
     * Test of rejudgeSolution method, of class RejudgeController.
     *
     * [RejudgeController.rejudgeSolution]
     */
    @Test
    @Throws(Exception::class)
    fun testRejudgeSolution() {
        assumeTrue(Arrays.asList(*environment!!.activeProfiles).contains("appveyor"), "not appveyor")
        log.info("rejudgeSolution")
        val solutionId = submission!!.id
        val result = mvc!!.perform(get("/admin/rejudge")
                .accept(MediaType.TEXT_HTML, MediaType.APPLICATION_JSON)
                .param("solution_id", java.lang.Long.toString(solutionId)))
                .andExpect(request().asyncStarted())
                .andReturn()
        mvc.perform(asyncDispatch(result))
                .andExpect(status().isOk)
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn()
    }

    /**
     * Test of rejudgeProblem method, of class RejudgeController.
     *
     * [RejudgeController.rejudgeProblem]
     */
    @Test
    @Throws(Exception::class)
    fun testRejudgeProblem() {
        log.info("rejudgeProblem")
        val problemId: Long = 0
        val result = mvc!!.perform(get("/admin/rejudge")
                .accept(MediaType.TEXT_HTML, MediaType.APPLICATION_JSON)
                .param("problem_id", java.lang.Long.toString(problemId)))
                .andExpect(status().isAccepted)
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn()
    }

    companion object {
        private val log = LoggerFactory.getLogger(RejudgeControllerTest::class.java)
    }
}
