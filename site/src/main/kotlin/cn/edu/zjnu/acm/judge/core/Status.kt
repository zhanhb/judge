package cn.edu.zjnu.acm.judge.core

/**
 * @author zhanhb
 */
enum class Status {

    ACCEPTED(0), PRESENTATION_ERROR(1), TIME_LIMIT_EXCEED(2), MEMORY_LIMIT_EXCEED(3),
    WRONG_ANSWER(4), RUNTIME_ERROR(5), OUTPUT_LIMIT_EXCEED(6), COMPILATION_ERROR(7),
    NON_ZERO_EXIT_CODE(8, "Non-zero Exit Code"), FLOATING_POINT_ERROR(9), SEGMENTATION_FAULT(10),
    RESTRICTED_FUNCTION(11),
    OUT_OF_CONTEST_TIME(100, "Out of Contest Time"), NO_SUCH_PROBLEM(101), SUBMISSION_LIMIT_EXCEED(102),
    MULTI_ERROR(200), PARTIALLY_CORRECT(201), UNACCEPTED(202), JUDGE_INTERNAL_ERROR(203),
    QUEUING(1000), PROCESSING(1001), COMPILING(1002), RUNNING(1003), VALIDATING(1004),
    JUDGING(1005), PENDING_REJUDGE(1006);

    val isFinalResult: Boolean
    private val toString: String
    val result: Int

    constructor(result: Int) {
        this.result = result
        this.isFinalResult = result < 1000
        this.toString = name.replace("(^|_)(?i)([A-Z]+)".toRegex()) { matcher ->
            val prefix = matcher.groups[1]!!
            val partial = matcher.groups[2]!!
            (if (prefix.value == "") "" else " ") + Character.toUpperCase(partial.value[0]) + partial.value.substring(1).toLowerCase()
        }
    }

    constructor(result: Int, toString: String) {
        this.result = result
        this.isFinalResult = result < 1000
        this.toString = toString
    }

    override fun toString(): String {
        return toString
    }

}
