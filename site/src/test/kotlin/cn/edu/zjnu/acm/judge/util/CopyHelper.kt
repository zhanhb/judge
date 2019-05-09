/*
 * Copyright 2014 zhanhb.
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
import java.nio.file.*
import java.nio.file.attribute.BasicFileAttributeView
import java.nio.file.attribute.BasicFileAttributes

class CopyHelper(
        private val src: Path,
        private val dest: Path,
        private vararg val options: CopyOption
) : SimpleFileVisitor<Path>() {

    private fun resolve(dir: Path): Path {
        return dest.resolve(src.relativize(dir).toString())
    }

    @Throws(IOException::class)
    override fun preVisitDirectory(dir: Path, attrs: BasicFileAttributes): FileVisitResult {
        val resolve = resolve(dir)
        if (!Files.exists(resolve)) {
            Files.createDirectories(resolve)
            val view = Files.getFileAttributeView(resolve, BasicFileAttributeView::class.java)
            view.setTimes(attrs.lastModifiedTime(), attrs.lastAccessTime(), attrs.creationTime())
        }
        return FileVisitResult.CONTINUE
    }

    @Throws(IOException::class)
    override fun visitFile(file: Path, attrs: BasicFileAttributes): FileVisitResult {
        Files.copy(file, resolve(file), *options)
        return FileVisitResult.CONTINUE
    }

    companion object {

        @Throws(IOException::class)
        fun copy(src: Path, dest: Path, vararg options: CopyOption): Path {
            return Files.walkFileTree(src, CopyHelper(src, dest, *options))
        }
    }
}
