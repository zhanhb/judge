package cn.edu.zjnu.acm.judge.problem;

import cn.edu.zjnu.acm.judge.domain.Problem;
import cn.edu.zjnu.acm.judge.exception.MessageException;
import cn.edu.zjnu.acm.judge.mapper.ProblemMapper;
import java.util.List;
import java.util.Locale;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.thymeleaf.util.StringUtils;

@Controller
public class SearchProblemController {

    @Autowired
    private ProblemMapper problemMapper;

    @RequestMapping(value = "/searchproblem", method = {RequestMethod.GET, RequestMethod.HEAD})
    protected String searchproblem(HttpServletRequest request,
            @RequestParam(value = "sstr", required = false) String query,
            Locale locale,
            Authentication authentication) {
        if (StringUtils.isEmpty(query)) {
            throw new MessageException("Please input the keyword to the problem.", HttpStatus.BAD_REQUEST);
        }
        String userId = authentication != null ? authentication.getName() : null;
        List<Problem> problems = problemMapper.findAllBySearchTitleOrSourceAndDisabledFalse(query, userId, locale.getLanguage());

        request.setAttribute("query", query);
        request.setAttribute("problems", problems);
        return "problems/search";
    }

}
