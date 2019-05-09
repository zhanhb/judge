/*
 * Copyright 2016 ZJNU ACM.
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

import cn.edu.zjnu.acm.judge.domain.LoginLog
import cn.edu.zjnu.acm.judge.mapper.LoginlogMapper
import cn.edu.zjnu.acm.judge.service.LoginlogService
import com.google.common.annotations.VisibleForTesting
import org.springframework.stereotype.Service
import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import javax.annotation.PostConstruct
import javax.annotation.PreDestroy

/**
 * @author zhanhb
 */
@Service("loginlogService")
class LoginlogServiceImpl(
        @Suppress("SpringJavaInjectionPointsAutowiringInspection") private val loginlogMapper: LoginlogMapper
) : LoginlogService {

    private var executorService: ExecutorService? = null
    private val list = ArrayBlockingQueue<LoginLog>(200)

    @PostConstruct
    fun init() {
        executorService = Executors.newSingleThreadExecutor()
    }

    @PreDestroy
    fun destroy() {
        executorService!!.shutdown()
    }

    override fun save(loginlog: LoginLog) {
        try {
            list.put(loginlog)
            executorService!!.submit { this.saveBatch() }
        } catch (ex: InterruptedException) {
            Thread.currentThread().interrupt()
        }

    }

    private fun saveBatch() {
        val tmp = ArrayList<LoginLog>(list.size)
        if (list.drainTo(tmp) > 0) {
            loginlogMapper.save(tmp)
        }
    }

    @VisibleForTesting
    @Throws(InterruptedException::class)
    fun await(timeout: Long, unit: TimeUnit): Boolean {
        return executorService!!.awaitTermination(timeout, unit)
    }

}
