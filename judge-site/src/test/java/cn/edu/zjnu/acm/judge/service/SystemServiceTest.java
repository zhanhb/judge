/*
 * Copyright 2015 Pivotal Software, Inc..
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
package cn.edu.zjnu.acm.judge.service;

import cn.edu.zjnu.acm.judge.Application;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author zhanhb
 */
@RunWith(JUnitPlatform.class)
@Slf4j
@SpringBootTest(classes = Application.class)
@Transactional
@WebAppConfiguration
public class SystemServiceTest {

    @Autowired
    private SystemService systemService;

    /**
     * Test of getDataDirectory method, of class SystemService.
     */
    @Test
    public void testGetDataDirectory() {
        log.info("getDataDirectory");
        long problemId = 0L;
        systemService.getDataDirectory(problemId);
    }

    /**
     * Test of getWorkDirectory method, of class SystemService.
     */
    @Test
    public void testGetWorkDirectory() {
        log.info("getWorkDirectory");
        long solutionId = 0L;
        systemService.getWorkDirectory(solutionId);
    }

    /**
     * Test of getUploadDirectory method, of class SystemService.
     */
    @Test
    public void testGetUploadDirectory() {
        log.info("getUploadDirectory");
        systemService.getUploadDirectory();
    }

}
