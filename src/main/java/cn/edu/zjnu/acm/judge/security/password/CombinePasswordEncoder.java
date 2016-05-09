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
package cn.edu.zjnu.acm.judge.security.password;

import java.util.Arrays;
import java.util.Objects;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 *
 * @author zhanhb
 */
public class CombinePasswordEncoder extends PasswordEncoderWrapper {

    private final PasswordEncoder[] encoders;

    public CombinePasswordEncoder(PasswordEncoder... passwordEncoders) {
        this(0, passwordEncoders);
    }

    public CombinePasswordEncoder(int index, PasswordEncoder... passwordEncoders) {
        super(passwordEncoders[index]);
        PasswordEncoder[] clone = passwordEncoders.clone();
        // null check
        Arrays.stream(clone).forEach(Objects::requireNonNull);
        encoders = clone;
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        return Arrays.stream(encoders)
                .anyMatch(encoder -> encoder.matches(rawPassword, encodedPassword));
    }

}
