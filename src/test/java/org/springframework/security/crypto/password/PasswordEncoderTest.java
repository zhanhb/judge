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
package org.springframework.security.crypto.password;

import cn.edu.zjnu.acm.judge.Application;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author zhanhb
 */
@RunWith(SpringJUnit4ClassRunner.class)
@Slf4j
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
public class PasswordEncoderTest {

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Test of matches method, of class PasswordEncoder.
     */
    @Test
    public void testMatches() {
        log.info("matches");
        String rawPassword = "123456";
        String encodedPassword = "123456";
        assertTrue(passwordEncoder.matches(rawPassword, encodedPassword));
        encodedPassword = "7c4a8d09ca3762af61e59520943dc26494f8941b";
        assertTrue(passwordEncoder.matches(rawPassword, encodedPassword));
        encodedPassword = "123456,123456";
        assertFalse(passwordEncoder.matches(rawPassword, encodedPassword));
        encodedPassword = "7c4a8d09ca3762af61e59520943dc26494f8941b,7c4a8d09ca3762af61e59520943dc26494f8941b";
        assertTrue(passwordEncoder.matches(rawPassword, encodedPassword));
    }

    /**
     * Test of encode method, of class PasswordEncoder.
     */
    @Test
    public void testEncode() {
        log.info("passwordEncoder");
        String rawPassword = "123456";
        String result = passwordEncoder.encode(rawPassword);
        assertTrue(BCrypt.checkpw(rawPassword, result));

    }

}
