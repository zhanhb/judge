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
package cn.edu.zjnu.acm.judge.config

import cn.edu.zjnu.acm.judge.Application
import com.github.zhanhb.ckfinder.connector.autoconfigure.CKFinderProperties
import org.junit.jupiter.api.Test
import org.junit.platform.runner.JUnitPlatform
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.web.WebAppConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.forwardedUrl
import org.springframework.transaction.annotation.Transactional

/**
 *
 * @author zhanhb
 */
@AutoConfigureMockMvc
@RunWith(JUnitPlatform::class)
@SpringBootTest(classes = [Application::class])
@Transactional
@WebAppConfiguration
class CKFinderTest {

    @Autowired
    private val mvc: MockMvc? = null
    @Autowired
    private val properties: CKFinderProperties? = null

    @Test
    @Throws(Exception::class)
    fun test() {
        val path = properties!!.servlet.path
        for (string in path) {
            mvc!!.perform(get(string))
                    .andExpect(forwardedUrl("/unauthorized"))
        }
    }

}
