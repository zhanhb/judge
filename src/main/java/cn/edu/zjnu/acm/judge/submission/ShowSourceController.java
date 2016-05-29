package cn.edu.zjnu.acm.judge.submission;

import cn.edu.zjnu.acm.judge.config.LanguageFactory;
import cn.edu.zjnu.acm.judge.domain.Submission;
import cn.edu.zjnu.acm.judge.exception.MessageException;
import cn.edu.zjnu.acm.judge.mapper.SubmissionMapper;
import cn.edu.zjnu.acm.judge.mapper.UserPerferenceMapper;
import cn.edu.zjnu.acm.judge.service.SubmissionService;
import cn.edu.zjnu.acm.judge.service.UserDetailService;
import cn.edu.zjnu.acm.judge.util.ResultType;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@Slf4j
public class ShowSourceController {

    @Autowired
    private SubmissionMapper submissionMapper;
    @Autowired
    private UserPerferenceMapper userPerferenceMapper;
    @Autowired
    private SubmissionService submissionService;
    @Autowired
    private LanguageFactory languageFactory;

    @RequestMapping(value = "/showsource", method = {RequestMethod.GET, RequestMethod.HEAD})
    public String showsource(HttpServletRequest request,
            @RequestParam("solution_id") long submissionId,
            @RequestParam(value = "style", required = false) Integer style) {
        UserDetailService.requireLoginned(request);
        Submission submission = submissionMapper.findOne(submissionId);

        if (submission == null) {
            throw new MessageException("No such solution", HttpStatus.NOT_FOUND);
        }
        String userId = UserDetailService.getCurrentUserId(request).orElse(null);
        if (!submissionService.canView(request, submission)) {
            throw new MessageException("You have no permission to view the source.", HttpStatus.FORBIDDEN);
        }
        String language = languageFactory.getLanguage(submission.getLanguage()).getName();

        if (style == null) {
            style = userPerferenceMapper.getStyle(userId);
        } else {
            userPerferenceMapper.setStyle(userId, style);
        }
        String source = submissionMapper.findSourceById(submissionId);

        request.setAttribute("submission", submission);
        if (submission.getContest() != null) {
            request.setAttribute("contestId", submission.getContest());
        }
        request.setAttribute("language", language);
        request.setAttribute("result", ResultType.getShowsourceString(submission.getScore()));
        request.setAttribute("style", style);
        request.setAttribute("source", source);
        return "submissions/source";
    }

}
