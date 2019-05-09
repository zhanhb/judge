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

import cn.edu.zjnu.acm.judge.config.Constants
import cn.edu.zjnu.acm.judge.domain.Submission
import cn.edu.zjnu.acm.judge.exception.BusinessCode
import cn.edu.zjnu.acm.judge.exception.BusinessException
import cn.edu.zjnu.acm.judge.service.ContestOnlyService
import javax.servlet.http.HttpServletRequest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cache.Cache
import org.springframework.cache.CacheManager
import org.springframework.stereotype.Service

/**
 *
 * @author zhanhb
 */
@Service("contestOnlyService")
class ContestOnlyServiceImpl : ContestOnlyService {
    private var cache: Cache? = null

    override var contestOnly: Long?
        get() = cache!!.get(KEY, java.lang.Long::class.java)?.toLong()
        set(contestOnly) = if (contestOnly == null) {
            cache!!.evict(KEY)
        } else {
            cache!!.put(KEY, contestOnly)
        }

    @Autowired
    fun setCacheManager(cacheManager: CacheManager) {
        cache = cacheManager.getCache(Constants.Cache.CONTEST_ONLY)
    }

    override fun checkSubmit(request: HttpServletRequest, contest: Long?, problemId: Long) {
        val contestOnly = contestOnly ?: return
        if (UserDetailsServiceImpl.isAdminLoginned(request)) {
            return
        }
        if (contest != contestOnly) {
            throw BusinessException(BusinessCode.CONTEST_ONLY_SUBMIT)
        }
    }

    override fun checkRegister() {
        contestOnly ?: return
        throw BusinessException(BusinessCode.CONTEST_ONLY_REGISTER)
    }

    override fun checkViewSource(request: HttpServletRequest, submission: Submission) {
        if (!canViewSource(request, submission)) {
            throw BusinessException(BusinessCode.CONTEST_ONLY_VIEW_SOURCE, submission.id)
        }
    }

    override fun canViewSource(request: HttpServletRequest, submission: Submission): Boolean {
        val contestOnly = contestOnly ?: return true
        if (UserDetailsServiceImpl.isAdminLoginned(request)) {
            return true
        }
        val contestId = submission.contest ?: return false
        return contestId == contestOnly
    }

    companion object {
        private const val KEY = "value"
    }

}
