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

import cn.edu.zjnu.acm.judge.domain.User
import cn.edu.zjnu.acm.judge.mapper.UserMapper
import cn.edu.zjnu.acm.judge.service.ResetPasswordService
import cn.edu.zjnu.acm.judge.util.Utility
import com.google.common.cache.Cache
import com.google.common.cache.CacheBuilder
import com.google.common.collect.ImmutableMap
import org.springframework.stereotype.Service
import java.util.concurrent.ConcurrentMap
import java.util.concurrent.TimeUnit
import javax.annotation.PostConstruct

/**
 *
 * @author zhanhb
 */
@Service("resetPasswordService")
class ResetPasswordServiceImpl(
        @Suppress("SpringJavaInjectionPointsAutowiringInspection") private val userMapper: UserMapper
) : ResetPasswordService {

    private var cache: Cache<String, String>? = null

    @PostConstruct
    fun init() {
        cache = CacheBuilder.newBuilder()
                .expireAfterWrite(1, TimeUnit.HOURS)
                .recordStats()
                .build()
    }

    override fun checkVcode(userId: String?, vcode: String?): User? {
        val user = if (userId != null) userMapper.findOne(userId) else null
        return if (user != null && asMap()[user.id!!] == vcode) user else null
    }

    override fun getOrCreate(id: String?): String {
        return asMap().compute(id) { _, old -> old ?: Utility.getRandomString(16) }!!
    }

    override fun get(id: String?): String? {
        return asMap()[id]
    }

    private fun asMap(): ConcurrentMap<String, String> {
        return cache!!.asMap()
    }

    override fun remove(userId: String?) {
        if (userId != null) cache!!.invalidate(userId)
    }

    override fun stats(): Map<String, *> {
        val stats = cache!!.stats()
        return ImmutableMap.of<String, Map<out Any, Any>>("content", asMap(), "stats",
                ImmutableMap.builder<String, Any>()
                        .put("hitCount", stats.hitCount())
                        .put("missCount", stats.missCount())
                        .put("loadSuccessCount", stats.loadSuccessCount())
                        .put("loadExceptionCount", stats.loadExceptionCount())
                        .put("totalLoadTime", stats.totalLoadTime())
                        .put("evictionCount", stats.evictionCount())
                        .build())
    }

}
