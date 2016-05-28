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
package cn.edu.zjnu.acm.judge.exception;

import java.io.IOException;
import java.net.URLEncoder;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.boot.autoconfigure.web.ErrorController;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler implements ErrorController {

    @ExceptionHandler(MessageException.class)
    public String messageExceptionHandler(HttpServletRequest request, HttpServletResponse response, MessageException ex) {
        String message = ex.getMessage();
        int code = ex.getCode();
        request.setAttribute("message", message);
        if (code % 100 == 4) {
            response.setStatus(code);
        }
        return "message";
    }

    @ExceptionHandler(ForbiddenException.class)
    public String forbiddenExceptionHandler(HttpServletRequest request)
            throws IOException {
        return "redirect:/login?url=" + URLEncoder.encode((String) request.getAttribute("backUrl"), "UTF-8");
    }

    @Override
    public String getErrorPath() {
        return "/error";
    }

}
