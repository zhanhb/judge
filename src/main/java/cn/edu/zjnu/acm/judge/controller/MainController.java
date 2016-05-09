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
package cn.edu.zjnu.acm.judge.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 *
 * @author zhanhb
 */
@Controller
public class MainController {

    @RequestMapping(value = "/", method = {RequestMethod.GET, RequestMethod.HEAD})
    public String index() {
        return "index";
    }

    @RequestMapping(value = "/faq", method = {RequestMethod.GET, RequestMethod.HEAD})
    public String faq() {
        return "faq";
    }

    @RequestMapping(value = "/findpassword", method = {RequestMethod.GET, RequestMethod.HEAD})
    public String findpassword() {
        return "findpassword";
    }

    @RequestMapping(value = "/notice", method = {RequestMethod.GET, RequestMethod.HEAD})
    public String notice() {
        return "fragment/notice";
    }

    @RequestMapping(value = "/registerpage", method = {RequestMethod.GET, RequestMethod.HEAD})
    public String registerpage() {
        return "registerpage";
    }

    @RequestMapping(value = "/navigation", method = {RequestMethod.GET, RequestMethod.HEAD})
    protected String navigation() {
        return "fragment/navigation";
    }

}
