package cn.edu.zjnu.acm.judge.service.impl

import cn.edu.zjnu.acm.judge.mapper.SubmissionMapper
import cn.edu.zjnu.acm.judge.service.JudgePoolService
import cn.edu.zjnu.acm.judge.service.RejudgeService
import java.util.concurrent.CompletableFuture
import org.springframework.stereotype.Service

@Service("rejudgeService")
class RejudgeServiceImpl(
        private val judgePoolService: JudgePoolService,
        private val submissionMapper: SubmissionMapper
) : RejudgeService {

    override fun byProblemId(problemId: Long): CompletableFuture<*> {
        val submissions = submissionMapper.findAllByProblemIdAndResultNotAccept(problemId)
        return judgePoolService.addAll(*submissions.stream().mapToLong({ it.toLong() }).toArray())
    }

    override fun bySubmissionId(submissionId: Long): CompletableFuture<*> {
        return judgePoolService.add(submissionId).thenApply { _ -> submissionMapper.findOne(submissionId) }
    }

}
