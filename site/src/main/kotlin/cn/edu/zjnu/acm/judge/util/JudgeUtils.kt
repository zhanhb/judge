package cn.edu.zjnu.acm.judge.util

import org.unbescape.html.HtmlEscape
import java.io.BufferedReader
import java.io.StringReader
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.stream.LongStream
import kotlin.streams.toList

/**
 * @author zhanhb
 */
@SpecialCall("fragment/standing", "users/list")
object JudgeUtils {

    /**
     * required in fragment/standing
     *
     * @param seconds the time, in seconds
     * @return A string represents the specified seconds
     */
    @SpecialCall("fragment/standing")
    fun formatTime(seconds: Long): String {
        @Suppress("NAME_SHADOWING") var seconds = seconds
        var neg = false
        if (seconds < 0) {
            neg = true
            seconds = -seconds
        }
        val h = seconds.ushr(4) / 225      // h = seconds/3600, unsigned
        val ms = (seconds - h * 3600).toInt()
        val m = ms / 60
        val s = ms - m * 60

        val buf = StringBuilder(8)
        if (neg) {
            buf.append('-')
        }
        if (h < 10) {
            buf.append('0')
        }
        buf.append(h).append(':')
        if (m < 10) {
            buf.append('0')
        }
        buf.append(m).append(':')
        if (s < 10) {
            buf.append('0')
        }
        return buf.append(s).toString()
    }

    fun getReplyString(string: String?): String? {
        return if (string.isNullOrBlank()) ""
        else HtmlEscape.escapeHtml4Xml(BufferedReader(StringReader(string)).lines()
                .toList()
                .filter { line -> !line.startsWith("> ") }
                .joinToString("\n> ", "> ", "\n"))
    }

    fun formatTime(a: Instant, b: Instant): String {
        return formatTime(ChronoUnit.SECONDS.between(a, b))
    }

    @SpecialCall("users/list")
    fun sequence(total: Long, current: Long): LongArray {
        if (total <= 0) {
            if (total == 0L) {
                return LongArray(0)
            }
            throw IllegalArgumentException()
        }
        val stream: LongStream
        val max = 15L
        if (total <= max) {
            stream = LongStream.range(0, total)
        } else {
            val a = LongStream.of(0, total - 1)
            val b: LongStream;
            val left: Long = max / 2;
            val right: Long = max - left - 1;

            if (current > total - right) {
                b = LongStream.range(total - max + 1, total);
            } else if (current < left) {
                b = LongStream.range(0, max - 1);
            } else {
                b = LongStream.range(Math.max(0, current - left + 1), Math.min(total, current + right));
            }
            stream = LongStream.concat(a, b)
        }
        return stream.sorted().distinct().toArray()
    }

}
