/*
 * Copyright 2018 ZJNU ACM.
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

import cn.edu.zjnu.acm.judge.service.DeleteService
import cn.edu.zjnu.acm.judge.util.DeleteHelper
import org.slf4j.LoggerFactory
import java.io.IOException
import java.nio.file.AccessDeniedException
import java.nio.file.DirectoryNotEmptyException
import java.nio.file.Path
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicReference
import javax.annotation.PostConstruct
import javax.annotation.PreDestroy

/**
 *
 * @author zhanhb
 */
class WindowsDeleteService : DeleteService {
    private var pool: ScheduledExecutorService? = null

    @PostConstruct
    fun init() {
        this.pool = Executors.newScheduledThreadPool(1)
    }

    @PreDestroy
    fun shutdown() {
        this.pool!!.shutdown()
    }

    override fun delete(path: Path): ScheduledFuture<*> {
        val task = AtomicReference<ScheduledFuture<*>>()
        task.set(this.pool!!.scheduleAtFixedRate({
            while (true) {
                try {
                    DeleteHelper.delete(path)
                } catch (var3: DirectoryNotEmptyException) {
                    break
                } catch (var3: AccessDeniedException) {
                    break
                } catch (var4: Error) {
                    log.error("", var4)
                } catch (var4: IOException) {
                    log.error("", var4)
                }
                task.get().cancel(false)
            }
        }, 0L, 5L, TimeUnit.SECONDS))
        return task.get()
    }

    companion object {
        private val log = LoggerFactory.getLogger(WindowsDeleteService::class.java)
    }
}
