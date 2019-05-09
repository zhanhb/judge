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
package cn.edu.zjnu.acm.judge.data.form

import java.util.EnumSet
import java.util.Locale

/**
 *
 * @author zhanhb
 */
enum class ContestStatus {

    PENDING, RUNNING, ENDED, ERROR;

    companion object {

        fun parse(filter: Array<String?>?): EnumSet<ContestStatus> {
            val set = EnumSet.noneOf(ContestStatus::class.java)
            if (filter != null) {
                for (exclude in filter) {
                    if (exclude != null) {
                        for (name in exclude.split("\\W+".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()) {
                            val contestStatus: ContestStatus
                            try {
                                contestStatus = valueOf(name.trim().toUpperCase(Locale.US))
                            } catch (ex: IllegalArgumentException) {
                                // TODO
                                continue
                            }

                            set.add(contestStatus)
                        }
                    }
                }
            }
            return set
        }
    }

}
