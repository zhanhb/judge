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

import cn.edu.zjnu.acm.judge.util.SpecialCall
import com.google.common.base.Preconditions
import com.google.common.collect.Maps

class UserStanding(val user: String) {
    var solved: Int = 0
        private set
    var time: Long = 0
        private set
    var index: Int = 0
        internal set
    var nick: String? = null
        internal set
    private val map = Maps.newHashMapWithExpectedSize<Long, ProblemTimePenalty>(50)

    internal fun add(problem: Long, time: Long?, penalty: Long) {
        Preconditions.checkArgument(penalty >= 0L, "penalty < 0")
        if (this.map.put(problem, ProblemTimePenalty(time, penalty)) != null) {
            throw IllegalStateException()
        } else {
            if (time != null) {
                ++this.solved
                this.time += time + 1200L * penalty
            }

        }
    }

    fun getTime(problem: Long): Long? {
        return this.map[problem]?.time
    }

    @SpecialCall("fragment/standing")
    fun getPenalty(problem: Long): Long {
        return this.map[problem]?.penalty ?: 0L
    }

    private class ProblemTimePenalty(internal val time: Long?, internal val penalty: Long)

    companion object {
        val COMPARATOR = Comparator.comparingInt<UserStanding>({ it.solved }).reversed().thenComparingLong({ it.time })
    }
}
