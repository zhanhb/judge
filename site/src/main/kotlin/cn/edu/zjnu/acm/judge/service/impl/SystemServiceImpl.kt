/*
 * Copyright 2017-2019 ZJNU ACM.
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
import cn.edu.zjnu.acm.judge.data.form.SystemInfoForm
import cn.edu.zjnu.acm.judge.mapper.SystemMapper
import cn.edu.zjnu.acm.judge.service.SystemService
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.flyway.FlywayMigrationInitializer
import org.springframework.stereotype.Service
import java.lang.IllegalStateException

/**
 *
 * @author zhanhb
 */
@Service("systemService")
class SystemServiceImpl(
        @Suppress("SpringJavaInjectionPointsAutowiringInspection") private val systemMapper: SystemMapper
) : SystemService {

    @Volatile
    override var systemInfo: SystemInfoForm? = null

    override val adminMail: String?
        get() = systemMapper.getValueByName(Constants.SystemKey.ADMIN_MAIL)

    override val ga: String?
        get() = systemMapper.getValueByName(Constants.SystemKey.GA)

    override val resetPasswordTitle: String?
        get() = systemMapper.getValueByName(Constants.SystemKey.RESETPASSWORD_TITLE)

    override var index: String?
        get() = systemMapper.getValueByName(Constants.SystemKey.PAGE_INDEX)
        set(index) {
            systemMapper.updateValueByName(Constants.SystemKey.PAGE_INDEX, index!!)
        }

    override val uploadDirectory: Path
        get() {
            val path = systemMapper.getValueByName(Constants.SystemKey.UPLOAD_PATH)
                    ?: throw IllegalStateException("upload directory absent")
            return Paths.get(path)
        }

    override val isDeleteTempFile: Boolean
        get() = java.lang.Boolean.parseBoolean(systemMapper.getValueByName(Constants.SystemKey.DELETE_TEMP_FILE))

    @Autowired // ensure flyway initialize before this service
    fun setFlywayMigrationInitializer(flywayMigrationInitializer: FlywayMigrationInitializer) {
    }

    override fun getDataDirectory(problem: Long): Path {
        val path = systemMapper.getValueByName(Constants.SystemKey.DATA_FILES_PATH)
                ?: throw IllegalStateException("data directory absent")
        return Paths.get(path, problem.toString())
    }

    override fun getWorkDirectory(submissionId: Long): Path {
        val path = systemMapper.getValueByName(Constants.SystemKey.WORKING_PATH)
                ?: throw IllegalStateException("working path absent")
        return Paths.get(path, submissionId.toString())
    }

    override fun getSpecialJudgeExecutable(problemId: Long): Path {
        return getDataDirectory(problemId).resolve(VALIDATE_FILE_NAME)
    }

    override fun isSpecialJudge(problemId: Long): Boolean {
        return Files.isExecutable(getSpecialJudgeExecutable(problemId))
    }

    companion object {

        private val VALIDATE_FILE_NAME = "compare.exe"
    }

}
