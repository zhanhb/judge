/*
 * Copyright 2019 ZJNU ACM.
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
package cn.edu.zjnu.acm.judge.support

import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.util.*
import kotlin.collections.ArrayList

/**
 *
 * @author zhanhb
 */
class JudgeData @Throws(IOException::class)
constructor(dataDirectory: Path) {

    private val data: Array<Pair<Path, Path>>

    val caseCount: Int
        get() = data.size

    init {
        if (!Files.isDirectory(dataDirectory)) {
            throw NoDataException("data directory not exists")
        }
        val files = ArrayList<Pair<Path, Path>>(20)
        Files.newDirectoryStream(dataDirectory).use { listFiles ->
            for (inFile in listFiles) {
                val inFileName = inFile.fileName.toString()
                if (!inFileName.toLowerCase().endsWith(".in")) {
                    continue
                }
                val outFile = dataDirectory.resolve(inFileName.substring(0, inFileName.length - 3) + ".out")
                if (!Files.exists(outFile)) {
                    continue
                }
                files.add(inFile to outFile)//统计输入,输出文件
            }
        }
        val caseNum = files.size
        if (caseNum == 0) {
            throw NoDataException("No test cases found in specified directory")
        }
        this.data = files.toTypedArray()
    }

    operator fun get(index: Int): Pair<Path, Path> {
        return data[index]
    }

    override fun toString(): String {
        return "JudgeData{data=" + Arrays.deepToString(data) + '}'.toString()
    }

}
