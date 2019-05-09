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
import com.google.common.base.Strings
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.platform.runner.JUnitPlatform
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.web.WebAppConfiguration
import org.springframework.transaction.annotation.Transactional
import java.nio.charset.StandardCharsets
import java.sql.SQLException
import javax.sql.DataSource

/**
 *
 * @author zhanhb
 */
@RunWith(JUnitPlatform::class)
@SpringBootTest(classes = [Application::class])
@Transactional
@WebAppConfiguration
class UnicodeTest {

    @Autowired
    private val dataSource: DataSource? = null

    @Test
    @Throws(SQLException::class)
    fun testUnicode() {
        val x = 0x1f602
        val laughCry = String(Character.toChars(x))
        test0(laughCry)
        for (i in 0..39) {
            test0(Strings.repeat(laughCry, i))
        }
    }

    @Throws(SQLException::class)
    private fun test0(laughCry: String) {
        dataSource!!.connection.use { connection ->
            connection.prepareStatement("CREATE TEMPORARY TABLE `test_table1`(`id` INT NOT NULL, `value` LONGTEXT NULL, PRIMARY KEY (`id`) ) COLLATE='utf8mb4_general_ci'").execute()
            try {
                connection.prepareStatement("insert into test_table1(id,value)values(1,?)").use { ps ->
                    ps.setBytes(1, laughCry.toByteArray(StandardCharsets.UTF_8))
                    ps.executeUpdate()
                }
                connection.prepareStatement("insert into test_table1(id,value)values(2,?)").use { ps ->
                    ps.setString(1, laughCry)
                    ps.executeUpdate()
                }
                connection.prepareStatement("select value from test_table1 where id in(1,2)").use { ps ->
                    ps.executeQuery().use { rs ->
                        while (rs.next()) {
                            assertThat(rs.getString(1)).isEqualTo(laughCry);
                        }
                    }
                }
            } finally {
                connection.prepareStatement("DROP TABLE `test_table1`").execute()
            }
        }
    }

}
