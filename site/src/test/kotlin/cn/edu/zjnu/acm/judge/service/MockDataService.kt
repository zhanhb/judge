/*
 * Copyright 2017 ZJNU ACM.
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
package cn.edu.zjnu.acm.judge.service

import cn.edu.zjnu.acm.judge.data.form.SubmissionQueryForm
import cn.edu.zjnu.acm.judge.domain.*
import cn.edu.zjnu.acm.judge.mapper.SubmissionMapper
import cn.edu.zjnu.acm.judge.util.Platform
import com.google.common.base.Throwables
import org.apache.poi.util.IOUtils
import org.junit.Assume.assumeTrue
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.io.IOException
import java.io.InterruptedIOException
import java.nio.charset.StandardCharsets
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.*
import java.util.concurrent.ExecutionException
import java.util.concurrent.ThreadLocalRandom
import java.util.concurrent.atomic.AtomicLong

/**
 *
 * @author zhanhb
 */
@Service
class MockDataService {

    private val idGenerator: (String) -> String

    @Autowired
    private val accountService: AccountService? = null
    @Autowired
    private val problemService: ProblemService? = null
    @Autowired
    private val contestService: ContestService? = null
    @Autowired
    private val submissionService: SubmissionService? = null
    @Suppress("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private val submissionMapper: SubmissionMapper? = null
    @Autowired
    private val languageService: LanguageService? = null

    init {
        val seed = AtomicLong(System.currentTimeMillis())
        idGenerator = { type -> type + seed.incrementAndGet() }
    }

    @JvmOverloads
    fun user(create: Boolean = true): User {
        return user({ it }, create)
    }

    @JvmOverloads
    fun user(function: (User) -> User, create: Boolean = true): User {
        val userId = idGenerator("user")
        val user = function(User(id = userId, password = userId, school = "", nick = userId))
        if (create) {
            accountService!!.save(user)
        }
        return user
    }

    @JvmOverloads
    fun problem(create: Boolean = true): Problem {
        return problem({ it }, create)
    }

    @JvmOverloads
    fun problem(function: (Problem) -> Problem, create: Boolean = true): Problem {
        val problem = function(Problem(
                title = "",
                description = "",
                input = "",
                output = "",
                sampleInput = "",
                sampleOutput = "",
                hint = "",
                source = "",
                timeLimit = 1000L,
                memoryLimit = 65536 * 1024L,
                contests = longArrayOf(0))
        )
        if (create) {
            problemService!!.save(problem)
        }
        return problem
    }

    @JvmOverloads
    fun contest(create: Boolean = true): Contest {
        val now = Instant.now()
        val contest = Contest(
                startTime = now.minus(1, ChronoUnit.HOURS),
                endTime = now.plus(1, ChronoUnit.HOURS),
                title = "test title",
                description = "test description"
        )
        if (create) {
            contestService!!.save(contest)
        }
        return contest
    }

    @Throws(Throwable::class)
    private fun submission(languageId: Int, source: String, userId: String?, ip: String,
                           problemId: Long, runTestCase: Boolean): Submission {
        if (runTestCase) {
            assumeTrue("not windows", Platform.isWindows)
        }
        try {
            submissionService!!.submit(languageId, source, userId!!, ip, problemId, runTestCase).get()
        } catch (ex: InterruptedException) {
            throw InterruptedIOException().initCause(ex)
        } catch (ex: ExecutionException) {
            throw ex.cause!!
        }

        return submissionMapper!!.findAllByCriteria(SubmissionQueryForm(
                user = userId,
                size = 1
        )).iterator().next()
    }

    @Throws(IOException::class)
    @JvmOverloads
    fun submission(runTestCase: Boolean = true): Submission {
        val userId = user().id
        val problemId = problem().id!!
        val source = Lazy.SAMPLE_SOURCE
        try {
            return submission(anyLanguage().id, source, userId, "::1", problemId, runTestCase)
        } catch (ex: Throwable) {
            Throwables.propagateIfPossible(ex, IOException::class.java)
            throw IOException(ex)
        }

    }

    fun anyLanguage(): Language {
        val languages = languageService!!.availableLanguages.values
        val list = ArrayList(languages)
        return list.get(ThreadLocalRandom.current().nextInt(list.size))
    }

    private object Lazy {

        internal var SAMPLE_SOURCE: String

        init {
            try {
                SAMPLE_SOURCE = String(IOUtils.toByteArray(Lazy::class.java.getResourceAsStream("/sample/program/ac/accept.cpp")), StandardCharsets.UTF_8)
            } catch (ex: IOException) {
                throw ExceptionInInitializerError(ex)
            }

        }
    }

}
