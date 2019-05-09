/*
 * Copyright 2014 zhanhb.
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
package cn.edu.zjnu.acm.judge.util

/**
 *
 * @author zhanhb
 */
@Deprecated("")
object ResultType {

    val ACCEPTED = 0
    val PRESENTATION_ERROR = 1
    val TIME_LIMIT_EXCEED = 2
    val MEMORY_LIMIT_EXCEED = 3
    val WRONG_ANSWER = 4
    val RUNTIME_ERROR = 5
    val OUTPUT_LIMIT_EXCEED = 6
    val COMPILE_ERROR = -7
    val SYSTEM_ERROR = -98
    val QUEUING = -10000
    val SCORE_ACCEPT = 100

    fun getCaseScoreDescription(score: Int): String {
        return when (score) {
            ACCEPTED, SCORE_ACCEPT -> "Accepted"
            PRESENTATION_ERROR -> "Presentation Error"
            TIME_LIMIT_EXCEED -> "Time Limit Exceed"
            MEMORY_LIMIT_EXCEED -> "Memory Limit Exceed"
            WRONG_ANSWER -> "Wrong Answer"
            RUNTIME_ERROR -> "Runtime Error"
            OUTPUT_LIMIT_EXCEED -> "Output Limit Exceed"
            COMPILE_ERROR -> "Compile Error"
            QUEUING -> "<font color=green>Waiting</font>"
            SYSTEM_ERROR -> "System Error"
            else -> "Other"
        }
    }

    fun getShowsourceString(pampd: Int): String {
        return when (pampd) {
            QUEUING, COMPILE_ERROR -> getCaseScoreDescription(pampd)
            SCORE_ACCEPT -> getCaseScoreDescription(0)
            else -> Integer.toString(pampd)
        }
    }

    fun getResultDescription(i: Int): String {
        return if (0 <= i && i < 100) "Unaccepted" else getCaseScoreDescription(i)
    }

}
