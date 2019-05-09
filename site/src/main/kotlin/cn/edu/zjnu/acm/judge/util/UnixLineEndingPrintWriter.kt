package cn.edu.zjnu.acm.judge.util

import java.io.OutputStream
import java.io.OutputStreamWriter
import java.io.PrintWriter

/**
 * @author zhanhb
 */
class UnixLineEndingPrintWriter(out: OutputStream) : PrintWriter(OutputStreamWriter(out, Platform.charset)) {

    override fun println() {
        super.write("\n")
        super.flush()
    }

}
