package cn.edu.zjnu.acm.judge.core

import java.io.IOException
import java.nio.file.Path

/**
 * @author zhanhb
 */
interface Validator {

    /**
     *
     * @param inputFile
     * @param standardOutput
     * @param outputFile
     * @return
     * @throws java.io.IOException
     * @see Status.ACCEPTED
     *
     * @see Status.PRESENTATION_ERROR
     *
     * @see Status.WRONG_ANSWER
     */
    @Throws(IOException::class)
    fun validate(inputFile: Path?, standardOutput: Path?, outputFile: Path?): Status

    @Throws(IOException::class)
    fun validate(options: Options, executeResult: ExecuteResult): ExecuteResult {
        val validate = validate(options.inputFile,
                options.standardOutput,
                options.outputFile)
        return executeResult.copy(code = validate)
    }

}
