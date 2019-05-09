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
package cn.edu.zjnu.acm.judge

import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Configuration
import java.io.OutputStreamWriter
import java.io.PrintStream
import java.lang.reflect.Field
import java.nio.charset.Charset

/**
 *
 * @author zhanhb
 */
// it's a component, we should have a no args constructor
// here we declared to avoid IDE warnings.
@Configuration
internal class SystemOutCharset {
    companion object {

        private val log = LoggerFactory.getLogger(SystemOutCharset::class.java)

        init {
            try {
                log.info("charset of System.out: {}", getCharset0(System.out))
                log.info("charset of System.err: {}", getCharset0(System.err))
                System.getProperties().store(System.out, null)
            } catch (ignore: ClassNotFoundException) {
            } catch (ignore: NoSuchFieldException) {
            } catch (ignore: SecurityException) {
            } catch (ex: Exception) {
                throw ExceptionInInitializerError(ex)
            }
        }

        @Throws(Exception::class)
        private operator fun get(field: Field, o: Any): Any {
            if (!field.isAccessible) {
                field.isAccessible = true
            }
            return field.get(o)
        }

        @Throws(Exception::class)
        private fun getCharset0(out: PrintStream): Charset {
            var o = get(PrintStream::class.java.getDeclaredField("charOut"), out)
            o = get(OutputStreamWriter::class.java.getDeclaredField("se"), o)
            return get(Class.forName("sun.nio.cs.StreamEncoder").getDeclaredField("cs"), o) as Charset
        }
    }

}
