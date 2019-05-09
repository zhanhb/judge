/*
 * Copyright 2015 zhanhb.
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
package test

import com.google.common.base.Strings
import org.junit.jupiter.api.Test
import org.junit.platform.runner.JUnitPlatform
import org.junit.runner.RunWith
import java.io.*
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.streams.asSequence

/**
 * @author zhanhb
 */
@RunWith(JUnitPlatform::class)
class SourceVisitor {

    @Test
    @Throws(IOException::class)
    fun test() {
        PrintStream("target/test.txt", "ISO-8859-1").use { target ->
            val dashes = Strings.repeat("-", 45)
            val sw = StringWriter()
            val out = PrintWriter(sw)
            Files.walk(Paths.get("src/main"))
                    .filter { path -> path.fileName.toString().matches(".*\\.(html|jspx?)".toRegex()) }
                    .forEach { path ->
                        try {
                            var string = String(Files.readAllBytes(path), StandardCharsets.ISO_8859_1)
                            string = string.replaceFirst("/\\*[\u0000-\uFFFF]+?org/licenses/LICENSE[\u0000-\uFFFF]+?\\*/\r?\n?".toRegex(), "")
                            string = string.replaceFirst("\r?\n?/\\*[\u0000-\uFFFF]+?@author zhanhb[\u0000-\uFFFF]+?\\*/".toRegex(), "")
                            out.println(dashes + path.fileName + dashes)
                            out.println(string)
                        } catch (ex: IOException) {
                            throw UncheckedIOException(ex)
                        }
                    }
            Files.walk(Paths.get("src/main"))
                    .filter { path -> path.fileName.toString().endsWith(".java") }
                    .filter { path -> path.fileName.toString() != "package-info.java" }
                    .forEach { path ->
                        try {
                            var string = String(Files.readAllBytes(path), StandardCharsets.ISO_8859_1)
                            if (!string.contains("@author zhanhb")) {
                                System.err.println(path)
                            } else {
                                string = string.replaceFirst("/\\*[\u0000-\uFFFF]+?org/licenses/LICENSE[\u0000-\uFFFF]+?\\*/\r?\n?".toRegex(), "")
                                string = string.replaceFirst("\r?\n?/\\*[\u0000-\uFFFF]+?@author zhanhb[\u0000-\uFFFF]+?\\*/".toRegex(), "")
                                out.println(dashes + path.fileName + dashes)
                                out.println(string)
                            }
                        } catch (ex: IOException) {
                            throw UncheckedIOException(ex)
                        }
                    }
            out.flush()
            val s = BufferedReader(StringReader(sw.toString()))
                    .lines().asSequence()
                    .filter { str -> !str.startsWith("import ") }
                    .filter { str -> !str.startsWith("package ") }
                    .joinToString("\n")
            target.print(s.replace("$dashes(\\r?\\n)+".toRegex(), dashes + "\\\n"))
        }
    }

}
