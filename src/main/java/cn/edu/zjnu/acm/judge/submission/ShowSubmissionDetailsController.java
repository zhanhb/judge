package cn.edu.zjnu.acm.judge.submission;

import cn.edu.zjnu.acm.judge.domain.Contest;
import cn.edu.zjnu.acm.judge.domain.Submission;
import cn.edu.zjnu.acm.judge.domain.SubmissionDetail;
import cn.edu.zjnu.acm.judge.exception.MessageException;
import cn.edu.zjnu.acm.judge.mapper.ContestMapper;
import cn.edu.zjnu.acm.judge.mapper.SubmissionMapper;
import cn.edu.zjnu.acm.judge.service.SubmissionService;
import cn.edu.zjnu.acm.judge.util.ResultType;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ShowSubmissionDetailsController {

    @Autowired
    private SubmissionMapper submissionMapper;
    @Autowired
    private ContestMapper contestMapper;
    @Autowired
    private SubmissionService submissionService;

    @RequestMapping(value = "/showsolutiondetails", method = {RequestMethod.GET, RequestMethod.HEAD})
    public String showsolutiondetails(HttpServletRequest request, @RequestParam("solution_id") long submissionId) {
        Submission submission = submissionMapper.findOne(submissionId);
        if (submission == null) {
            throw new MessageException("No such solution", HttpStatus.NOT_FOUND);
        }
        Long contestId = submission.getContest();
        if (contestId != null) {
            Contest contest = contestMapper.findOne(contestId);
            if (contest != null) {
                request.setAttribute("contestId", contest.getId());
            }
        }
        if (!submissionService.canView(request, submission)) {
            throw new MessageException("You have no permission to view solution '" + submissionId + "'", HttpStatus.FORBIDDEN);
        }
        String submissionDetail = submissionMapper.getSubmissionDetail(submissionId);
        if (submissionDetail == null) {
            submissionDetail = "";
        }

        String[] detailsArray = submissionDetail.split(",");
        SubmissionDetail[] details = new SubmissionDetail[detailsArray.length / 4];
        for (int i = 0; i < detailsArray.length / 4; ++i) {
            details[i] = SubmissionDetail.builder()
                    .result(ResultType.getCaseScoreDescription(Integer.parseInt(detailsArray[i << 2])))
                    .score(detailsArray[i << 2 | 1])
                    .time(detailsArray[i << 2 | 2])
                    .memory(detailsArray[i << 2 | 3])
                    .build();
        }
        request.setAttribute("details", details);
        request.setAttribute("user", submission.getUser());
        request.setAttribute("problem", submission.getProblem());
        request.setAttribute("result", ResultType.getResultDescription(submission.getScore()));
        request.setAttribute("score", submission.getScore());
        request.setAttribute("time", submission.getTime());
        request.setAttribute("memory", submission.getMemory());
        return "submissions/detail";
    }

}
