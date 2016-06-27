package cn.edu.zjnu.acm.judge.admin;

import cn.edu.zjnu.acm.judge.domain.Contest;
import cn.edu.zjnu.acm.judge.domain.Problem;
import cn.edu.zjnu.acm.judge.exception.MessageException;
import cn.edu.zjnu.acm.judge.mapper.ContestMapper;
import cn.edu.zjnu.acm.judge.mapper.ProblemMapper;
import cn.edu.zjnu.acm.judge.service.ContestService;
import cn.edu.zjnu.acm.judge.service.UserDetailService;
import java.util.Locale;
import java.util.Objects;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ModifyProblemController {

    @Autowired
    private ContestMapper contestMapper;
    @Autowired
    private ContestService contestService;
    @Autowired
    private ProblemMapper problemMapper;

    @RequestMapping(value = "/admin/problems/{problemId}", method = RequestMethod.PUT)
    public String modifyproblem(HttpServletRequest request,
            @PathVariable("problemId") long problemId,
            Locale locale,
            @RequestParam("problemLang") String lang,
            Problem p) {
        UserDetailService.requireAdminLoginned(request);

        Problem problem = problemMapper.findOne(problemId, lang);
        if (problem == null) {
            throw new MessageException("no such problem " + problemId, HttpStatus.NOT_FOUND);
        }
        Long oldContestId = problem.getContest();
        Long contestId = p.getContest();

        problemMapper.touchI18n(problemId, lang);
        problemMapper.update(problem.toBuilder()
                .title(p.getTitle())
                .description(p.getDescription())
                .input(p.getInput())
                .output(p.getOutput())
                .sampleInput(p.getSampleInput())
                .sampleOutput(p.getSampleOutput())
                .hint(p.getHint())
                .source(p.getSource())
                .timeLimit(p.getTimeLimit())
                .memoryLimit(p.getMemoryLimit())
                .contest(p.getContest())
                .build(), lang);
        if (oldContestId != null && !Objects.equals(oldContestId, contestId)) {
            boolean started = contestMapper.findOneByIdAndDefunctN(oldContestId).isStarted();
            if (!started) {
                contestService.removeProblem(oldContestId, problemId);
            }
        }
        if (contestId != null) {
            contestMapper.updateProblemTitle(contestId, problemId);
            if (!Objects.equals(oldContestId, contestId)) {
                Contest newContest = contestMapper.findOneByIdAndDefunctN(contestId);
                if (newContest == null) {
                    throw new MessageException("onlinejudge.contest.nosuchcontest", HttpStatus.NOT_FOUND);
                }
                boolean started = newContest.isStarted();
                if (!started) {
                    contestService.addProblem(contestId, problemId);
                }
            }
        }
        request.setAttribute("problemId", problemId);
        return "admin/problems/modify";
    }

}
