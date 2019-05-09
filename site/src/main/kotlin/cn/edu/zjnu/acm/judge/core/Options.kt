package cn.edu.zjnu.acm.judge.core

import java.nio.file.Path

/**
 * @author zhanhb
 */
data class Options(
        val inputFile: Path?,
        val errFile: Path?,
        val outputFile: Path?, // 提交程序的输出文件
        val standardOutput: Path?, // 标程输出的文件
        val timeLimit: Long = Long.MAX_VALUE,
        val memoryLimit: Long = Long.MAX_VALUE,
        val outputLimit: Long = Long.MAX_VALUE,
        val isRedirectErrorStream: Boolean = false,
        val command: String,
        val workDirectory: Path?
)
