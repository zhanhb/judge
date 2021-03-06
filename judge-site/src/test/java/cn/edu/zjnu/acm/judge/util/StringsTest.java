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
package cn.edu.zjnu.acm.judge.util;

import java.util.concurrent.ThreadLocalRandom;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

import static org.assertj.core.api.Assertions.assertThat;

/**
 *
 * @author zhanhb
 */
@RunWith(JUnitPlatform.class)
@Slf4j
public class StringsTest {

    // use null class loader, ensure access of system script engine manager.
    // usually our classloader will extends system class loader.
    // but surefire won't do like this. when not forking
    // the enigine manager can be found though system class loader.
    private final ScriptEngine javascript = new ScriptEngineManager(null).getEngineByName("javascript");

    @Test
    public void testConstructor() throws Throwable {
        TestUtils.testConstructor(Strings.class);
    }

    /**
     * Test of slice method, of class Strings.
     *
     * @throws javax.script.ScriptException
     */
    @Test
    public void testSlice_String_int() throws ScriptException {
        log.info("slice");

        ThreadLocalRandom random = ThreadLocalRandom.current();
        int length = random.nextInt(5) + 20;
        for (int i = 0; i < 30; ++i) {
            String randomString = random.ints(length, 'a', 'z' + 1)
                    .collect(StringBuilder::new,
                            (sb, x) -> sb.append((char) x),
                            StringBuilder::append).toString();
            assertThat(randomString.length()).isEqualTo(length);

            for (int j = 0; j < 30; ++j) {
                int start = random.nextInt(length * 6) - length * 3;
                String result = Strings.slice(randomString, start);

                String expResult = (String) javascript.eval("\'" + randomString + "\'.slice(" + start + ")");
                assertThat(result).isEqualTo(expResult);
            }
        }
    }

    /**
     * Test of slice method, of class Strings.
     *
     * @throws javax.script.ScriptException
     */
    @Test
    public void testSlice_3args() throws ScriptException {
        log.info("slice");

        ThreadLocalRandom random = ThreadLocalRandom.current();
        for (int i = 0; i < 30; ++i) {
            int length = random.nextInt(5) + 20;
            String randomString = random.ints(length, 'a', 'z' + 1)
                    .collect(StringBuilder::new,
                            (sb, x) -> sb.append((char) x),
                            StringBuilder::append).toString();
            assertThat(randomString.length()).isEqualTo(length);

            for (int j = 0; j < 30; ++j) {
                int start = random.nextInt(length * 6) - length * 3;
                int end = random.nextInt(length * 6) - length * 3;
                String result = Strings.slice(randomString, start, end);

                String expResult = (String) javascript.eval("\'" + randomString + "\'.slice(" + start + "," + end + ")");
                assertThat(result).isEqualTo(expResult);
            }
        }
    }

    @Test
    public void testNull() {
        log.info("slice");
        assertThat(Strings.slice(null, 0)).isNull();
        assertThat(Strings.slice(null, 1)).isNull();
        assertThat(Strings.slice(null, -1000)).isNull();
        assertThat(Strings.slice(null, 0, 0)).isNull();
    }

}
