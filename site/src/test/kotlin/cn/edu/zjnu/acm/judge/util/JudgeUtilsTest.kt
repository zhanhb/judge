package cn.edu.zjnu.acm.judge.util

import org.junit.jupiter.api.Test
import org.junit.platform.runner.JUnitPlatform
import org.junit.runner.RunWith
import org.springframework.transaction.annotation.Transactional

import org.assertj.core.api.Assertions.assertThat
import org.slf4j.LoggerFactory

/**
 *
 * @author zhanhb
 */
@RunWith(JUnitPlatform::class)
@Transactional
class JudgeUtilsTest {

    /**
     * Test of formatTime method, of class JudgeUtils.
     */
    @Test
    fun testFormatTime() {
        log.info("formatTime")
        val instance = JudgeUtils
        assertThat(instance.formatTime(0)).isEqualTo("00:00:00")
        assertThat(instance.formatTime(3675)).isEqualTo("01:01:15")
        assertThat(instance.formatTime(-3675)).isEqualTo("-01:01:15")
        assertThat(instance.formatTime(-3540)).isEqualTo("-00:59:00")
        assertThat(instance.formatTime(-3599)).isEqualTo("-00:59:59")
        assertThat(instance.formatTime(java.lang.Long.MAX_VALUE)).isEqualTo("2562047788015215:30:07")
        assertThat(instance.formatTime(java.lang.Long.MIN_VALUE)).isEqualTo("-2562047788015215:30:08")
        for (i in 0..3599) {
            val result = instance.formatTime(i.toLong())
            assertThat(result).isEqualTo("00:" + i / 600 + i / 60 % 10 + ':'.toString() + i % 60 / 10 + i % 10)
        }
    }

    companion object {
        private val log = LoggerFactory.getLogger(JudgeUtilsTest::class.java)
    }
}
