/*
 * Copyright 2019 ZJNU ACM.
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

import cn.edu.zjnu.acm.judge.domain.Submission
import com.google.common.annotations.VisibleForTesting
import java.util.concurrent.CompletableFuture
import javax.servlet.http.HttpServletRequest
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import cn.edu.zjnu.acm.judge.data.dto.SubmissionDetailDTO

/**
 *
 * @author zhanhb
 */
interface SubmissionService {

    fun bestSubmission(contestId: Long?, problemId: Long, pageable: Pageable, total: Long): Page<Submission>

    fun canView(request: HttpServletRequest, submission: Submission): Boolean

    fun contestSubmit(languageId: Int, source: String, userId: String, ip: String, contestId: Long, problemNum: Long): CompletableFuture<*>

    fun delete(id: Long)

    fun findCompileInfo(submissionId: Long): String?

    fun submit(languageId: Int, source: String, userId: String, ip: String, problemId: Long, addToPool: Boolean): CompletableFuture<*>

    @VisibleForTesting
    fun remove(userId: String)

    @VisibleForTesting
    fun parseSubmissionDetail(message: String): List<SubmissionDetailDTO>

    fun getSubmissionDetail(submissionId: Long): List<SubmissionDetailDTO>
}
