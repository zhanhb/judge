package cn.edu.zjnu.acm.judge.core

import java.io.IOException
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import java.util.Scanner

/**
 * @author zhanhb
 */
enum class SimpleValidator : Validator {
    NORMAL {
        @Throws(IOException::class)
        override fun validate(inputFile: Path?, outputFile: Path?, answerFile: Path?): Status {
            try {
                if (isAccepted(outputFile, answerFile)) {
                    return Status.ACCEPTED
                }
                if (isPresentationError(outputFile, answerFile)) {
                    return Status.PRESENTATION_ERROR
                }
            } catch (ignored: OutOfMemoryError) {
            }

            return Status.WRONG_ANSWER
        }
    },
    PE_AS_ACCEPTED {
        @Throws(IOException::class)
        override fun validate(inputFile: Path?, outputFile: Path?, answerFile: Path?): Status {
            try {
                if (isPresentationError(outputFile, answerFile)) {
                    return Status.ACCEPTED
                }
            } catch (ignored: OutOfMemoryError) {
            }

            return Status.WRONG_ANSWER
        }
    };

    @Throws(IOException::class)
    internal fun isAccepted(standardFile: Path?, answerFile: Path?): Boolean {
        Files.newBufferedReader(standardFile!!, StandardCharsets.ISO_8859_1).use { outr ->
            Files.newBufferedReader(answerFile!!, StandardCharsets.ISO_8859_1).use { ansr ->
                while (true) {
                    val linea = outr.readLine()
                    val lineb = ansr.readLine()
                    if (linea == null && lineb == null) {
                        return true
                    } else if (linea != lineb) {
                        return false
                    }
                }
            }
        }
        throw JudgeException()
    }

    @Throws(IOException::class)
    internal fun isPresentationError(standardFile: Path?, answerFile: Path?): Boolean {
        Files.newBufferedReader(standardFile!!, StandardCharsets.ISO_8859_1).use { outr ->
            Files.newBufferedReader(answerFile!!, StandardCharsets.ISO_8859_1).use { ansr ->
                Scanner(outr).use { out ->
                    Scanner(ansr).use { ans ->
                        while (out.hasNext()) {
                            if (!ans.hasNext() || out.next() != ans.next()) {
                                return false
                            }
                        }
                        return !ans.hasNext()
                    }
                }
            }
        }
    }

    @Throws(IOException::class)
    abstract override fun validate(inputFile: Path?, standardOutput: Path?, outputFile: Path?): Status

}
