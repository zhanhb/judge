/*
 * Copyright 2017 ZJNU ACM.
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
package cn.edu.zjnu.acm.judge.util

import java.io.IOException
import java.nio.file.FileVisitResult
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.SimpleFileVisitor
import java.nio.file.attribute.BasicFileAttributes

/**
 *
 * @author zhanhb
 */
internal class DeleteFileVisitor : SimpleFileVisitor<Path>() {

    override fun preVisitDirectory(dir: Path, attrs: BasicFileAttributes): FileVisitResult {
        try {
            Files.deleteIfExists(dir)
            return FileVisitResult.SKIP_SUBTREE
        } catch (ex: IOException) {
            return FileVisitResult.CONTINUE
        }

    }

    @Throws(IOException::class)
    override fun visitFile(file: Path, attrs: BasicFileAttributes): FileVisitResult {
        Files.deleteIfExists(file)
        return FileVisitResult.CONTINUE
    }

    @Throws(IOException::class)
    override fun postVisitDirectory(dir: Path, exc: IOException?): FileVisitResult {
        super.postVisitDirectory(dir, exc)
        Files.deleteIfExists(dir)
        return FileVisitResult.CONTINUE
    }

    companion object {
        val INSTANCE = DeleteFileVisitor()
    }

}
