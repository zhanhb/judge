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
package cn.edu.zjnu.acm.judge.mapper;

import cn.edu.zjnu.acm.judge.Application;
import cn.edu.zjnu.acm.judge.data.dto.Standing;
import cn.edu.zjnu.acm.judge.domain.Contest;
import cn.edu.zjnu.acm.judge.domain.Problem;
import cn.edu.zjnu.acm.judge.domain.User;
import cn.edu.zjnu.acm.judge.service.LocaleService;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 *
 * @author zhanhb
 */
@RunWith(JUnitPlatform.class)
@Slf4j
@SpringBootTest(classes = Application.class)
@Transactional
@WebAppConfiguration
public class ContestMapperTest {

    @Autowired
    private ContestMapper instance;
    @Autowired
    private LocaleService localeService;
    private final Locale locale = Locale.SIMPLIFIED_CHINESE;

    /**
     * Test of standing method, of class ContestMapper.
     */
    @Test
    public void testStanding() {
        log.info("standing");
        long contest = 1058L;
        List<Standing> result = instance.standing(contest);
        log.info("{}", result);
    }

    /**
     * Test of getProblems method, of class ContestMapper.
     */
    @Test
    public void testGetProblems() {
        log.info("getProblems");
        long contestId = 0L;
        List<Problem> expResult = Arrays.asList();
        List<Problem> result = instance.getProblems(contestId, null, localeService.resolve(locale));
        assertThat(result).isEqualTo(expResult);
    }

    /**
     * Test of getProblems method, of class ContestMapper.
     */
    @Test
    public void testGetProblemsNullLocale() {
        log.info("getProblems");
        long contestId = 0L;
        List<Problem> expResult = Arrays.asList();
        List<Problem> result = instance.getProblems(contestId, null, null);
        assertThat(result).isEqualTo(expResult);
    }

    /**
     * Test of getProblems method, of class ContestMapper.
     */
    @Test
    public void testGetUserProblems() {
        log.info("getProblems");
        long contestId = 0L;
        String userId = "'";
        List<Problem> expResult = Arrays.asList();
        List<Problem> result = instance.getProblems(contestId, userId, localeService.resolve(locale));
        assertThat(result).isEqualTo(expResult);
    }

    /**
     * Test of findOne method, of class ContestMapper.
     */
    @Test
    public void testFindOne() {
        log.info("findOne");
        long contestId = 0L;
        Contest expResult = null;
        Contest result = instance.findOne(contestId);
        assertThat(result).isEqualTo(expResult);
    }

    /**
     * Test of getProblem method, of class ContestMapper.
     */
    @Test
    public void testGetProblem() {
        log.info("getProblem");
        long contestId = 0L;
        long problemOrder = 0L;
        Problem expResult = null;
        Problem result = instance.getProblem(contestId, problemOrder, locale.toLanguageTag());
        assertThat(result).isEqualTo(expResult);
    }

    /**
     * Test of findOneByIdAndNotDisabled method, of class ContestMapper.
     */
    @Test
    public void testFindOneByIdAndDisabledFalse() {
        log.info("findOneByIdAndDisabledFalse");
        long contestId = 0L;
        Contest expResult = null;
        Contest result = instance.findOneByIdAndNotDisabled(contestId);
        assertThat(result).isEqualTo(expResult);
    }

    /**
     * Test of attenders method, of class ContestMapper.
     */
    @Test
    public void testAttenders() {
        log.info("attenders");
        long contestId = 0L;
        List<User> expResult = Arrays.asList();
        List<User> result = instance.attenders(contestId);
        assertThat(result).isEqualTo(expResult);
    }

}
