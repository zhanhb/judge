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
package cn.edu.zjnu.acm.judge.service.impl

import cn.edu.zjnu.acm.judge.domain.Message
import cn.edu.zjnu.acm.judge.exception.BusinessCode
import cn.edu.zjnu.acm.judge.exception.BusinessException
import cn.edu.zjnu.acm.judge.mapper.MessageMapper
import cn.edu.zjnu.acm.judge.service.MessageService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 *
 * @author zhanhb
 */
@Service("messageService")
class MessageServiceImpl(
        @Suppress("SpringJavaInjectionPointsAutowiringInspection") private val messageMapper: MessageMapper
) : MessageService {

    @Transactional
    override fun save(parentId: Long?, problemId: Long?, userId: String, title: String, content: String) {
        var depth: Long = 0
        var order: Long = 0

        val nextId = messageMapper.nextId()
        var parent: Message? = null
        if (parentId != null) {
            parent = messageMapper.findOne(parentId)
            if (parent == null) {
                throw BusinessException(BusinessCode.MESSAGE_NO_SUCH_PARENT, parentId)
            }
            order = parent.order
            val depth1 = parent.depth

            val messages = messageMapper.findAllByThreadIdAndOrderNumGreaterThanOrderByOrderNum(parent.thread, parent.order)
            for ((_, _, _, _, _, _, _, depth2, _, order1) in messages) {
                depth = depth2
                if (depth <= depth1) {
                    break
                }
                order = order1
            }
            depth = depth1 + 1
            messageMapper.updateOrderNumByThreadIdAndOrderNumGreaterThan(parent.thread, order)
            ++order
        }
        messageMapper.save(nextId, parentId, order, problemId, depth, userId, title, content)
        if (parent != null) {
            messageMapper.updateThreadIdByThreadId(nextId, parent.thread)
        }
    }
}
