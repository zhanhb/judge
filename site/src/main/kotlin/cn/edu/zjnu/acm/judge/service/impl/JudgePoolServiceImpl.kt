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
package cn.edu.zjnu.acm.judge.service.impl

import cn.edu.zjnu.acm.judge.service.JudgePoolService
import cn.edu.zjnu.acm.judge.service.JudgeService
import org.springframework.stereotype.Service
import java.util.concurrent.*
import java.util.concurrent.atomic.AtomicInteger
import javax.annotation.PostConstruct
import javax.annotation.PreDestroy
import java.util.concurrent.CompletableFuture

/**
 *
 * @author zhanhb
 */
@Service("judgePoolService")
class JudgePoolServiceImpl(
        private val judgeService: JudgeService
) : JudgePoolService {

    private var executorService: ExecutorService? = null

    @PostConstruct
    fun init() {
        val group = ThreadGroup("judge group")
        val counter = AtomicInteger()
        val threadFactory = ThreadFactory { runnable -> Thread(group, runnable, "judge thread " + counter.incrementAndGet()) }
        val nThreads = Runtime.getRuntime().availableProcessors()
        executorService = LogThreadPoolExecutor(nThreads, nThreads,
                0L, TimeUnit.MILLISECONDS,
                LinkedBlockingQueue<Runnable>(),
                threadFactory)
    }

    @PreDestroy
    fun destroy() {
        executorService!!.shutdownNow()
    }

    override fun add(id: Long): CompletableFuture<*> {
        return CompletableFuture.runAsync(Runnable { judgeService.execute(id) }, executorService)
    }

    override fun addAll(vararg ids: Long): CompletableFuture<*> {
        return CompletableFuture.allOf(*ids.asList().map { add(it) }.toTypedArray())
    }

}
