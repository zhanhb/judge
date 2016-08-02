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
package cn.edu.zjnu.acm.judge.config;

import cn.edu.zjnu.acm.judge.service.UserDetailService;
import com.ckfinder.connector.configuration.ConfigurationFactory;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author zhanhb
 */
public class ConnectorServlet extends com.ckfinder.connector.ConnectorServlet {

    private static final long serialVersionUID = 1L;

    ConnectorServlet(ConfigurationFactory configurationFactory) {
        super(configurationFactory);
    }

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!UserDetailService.isAdminLoginned(request)) {
            request.getRequestDispatcher("/unauthorized").forward(request, response);
            return;
        }
        super.service(request, response);
    }

}
