package cn.edu.zjnu.acm.judge.controller.user

import cn.edu.zjnu.acm.judge.mapper.UserProblemMapper
import cn.edu.zjnu.acm.judge.service.AccountService
import org.springframework.http.MediaType
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import java.util.BitSet

/**
 * @author zhanhb
 */
@Controller
@RequestMapping(produces = [MediaType.TEXT_HTML_VALUE])
class UserCompareController(
        @Suppress("SpringJavaInjectionPointsAutowiringInspection") private val userProblemMapper: UserProblemMapper,
        private val accountService: AccountService
) {

    private fun calc(a: BitSet, op: (BitSet, BitSet) -> Unit, b: BitSet): IntArray {
        val bitset = a.clone() as BitSet
        op(bitset, b)
        return bitset.stream().toArray()
    }

    @GetMapping("usercmp")
    fun compare(model: Model,
                @RequestParam("uid1") userId1: String,
                @RequestParam("uid2") userId2: String): String {
        accountService.findOne(userId1)
        accountService.findOne(userId2)
        val aac = BitSet()
        val awa = BitSet()
        val bac = BitSet()
        val bwa = BitSet()
        fill(userId1, aac, awa)
        fill(userId2, bac, bwa)

        model.addAttribute("uid1", userId1)
        model.addAttribute("uid2", userId2)
        model.addAttribute("a", calc(aac, { obj, set -> obj.andNot(set) }, bac))
        model.addAttribute("b", calc(bac, { obj, set -> obj.andNot(set) }, aac))
        model.addAttribute("c", calc(bac, { obj, set -> obj.and(set) }, aac))
        model.addAttribute("d", calc(awa, { obj, set -> obj.andNot(set) }, bwa))
        model.addAttribute("e", calc(bwa, { obj, set -> obj.andNot(set) }, awa))
        model.addAttribute("f", calc(awa, { obj, set -> obj.and(set) }, bwa))
        return "users/compare"
    }

    private fun fill(userId: String, ac: BitSet, wa: BitSet) {
        for (up in userProblemMapper.findAllByUserId(userId)) {
            val problem = up.problem
            val accepted = up.accepted
            val submit = up.submit
            val index = problem.toInt()
            if (accepted != 0L) {
                ac.set(index)
            } else if (submit != 0L) {
                wa.set(index)
            }
        }
    }

}
