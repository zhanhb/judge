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

import cn.edu.zjnu.acm.judge.data.form.SystemInfoForm
import cn.edu.zjnu.acm.judge.util.SpecialCall
import java.nio.file.Path

/**
 *
 * @author zhanhb
 */
@SpecialCall("index", "manager", "layout/main", "fragment/ga")
interface SystemService {

    var index: String?

    @get:SpecialCall("index", "manager", "layout/main")
    val adminMail: String?

    val isDeleteTempFile: Boolean

    @get:SpecialCall("fragment/ga")
    val ga: String?

    val resetPasswordTitle: String?

    val uploadDirectory: Path

    @get:SpecialCall("fragment/notice")
    var systemInfo: SystemInfoForm?

    fun getDataDirectory(problem: Long): Path

    fun getWorkDirectory(submissionId: Long): Path

    fun isSpecialJudge(problemId: Long): Boolean

    fun getSpecialJudgeExecutable(problemId: Long): Path
}
