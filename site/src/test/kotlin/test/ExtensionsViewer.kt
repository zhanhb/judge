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
package test

import com.google.common.collect.Lists
import com.google.common.collect.Maps
import org.apache.commons.compress.utils.IOUtils
import org.junit.jupiter.api.Test
import org.junit.platform.runner.JUnitPlatform
import org.junit.runner.RunWith
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.streams.asSequence

/**
 * @author zhanhb
 */
@RunWith(JUnitPlatform::class)
class ExtensionsViewer {

    /**
     * @throws java.io.IOException
     */
    @Test
    @Throws(IOException::class)
    fun test() {
        val process = ProcessBuilder()
                .command("git", "ls-files", "-z", "--", ":(glob,top,exclude).gitattributes")
                .redirectErrorStream(true).start()
        val map = Maps.newLinkedHashMap<String, List<Path>>()
        process.inputStream.use { `is` ->
            InputStreamReader(`is`, StandardCharsets.UTF_8).use { ir ->
                BufferedReader(ir).use { br ->
                    val sb = StringBuilder(20)
                    while (true) {
                        val x = br.read()
                        if (x == -1) {
                            break
                        }
                        if (x != 0) {
                            sb.append(x.toChar())
                            continue
                        }
                        val path = Paths.get(sb.toString())
                        val extension = getExtension(path)
                        if (extension.isEmpty()) {
                            continue
                        }
                        val list = map.computeIfAbsent(extension) { Lists.newArrayList() } as MutableList<Path>
                        list.add(path)
                        sb.setLength(0)
                    }
                }
            }
        }

        val gitRoot = String(IOUtils.toByteArray(
                ProcessBuilder().command("git", "rev-parse", "--show-toplevel")
                        .start().inputStream), StandardCharsets.UTF_8).trim { it <= ' ' }

        val set = Files.lines(Paths.get(gitRoot, ".gitattributes"))
                .asSequence()
                .map { it.trim() }
                .filter { str -> str.startsWith("*.") }
                .map { str -> str.replace("^\\*\\.|\\s.+$".toRegex(), "") }
                .toSet()
        map.keys.removeIf { it in set }
        println(map)
    }

    private fun getExtension(path: Path): String {
        val name = path.fileName.toString()
        val lastIndexOf = name.lastIndexOf('.')
        return if (lastIndexOf > 0) name.substring(lastIndexOf + 1) else ""
    }

}
