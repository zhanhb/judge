/*
 * Copyright 2017 ZJNU ACM.
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
package cn.edu.zjnu.acm.judge.exception

import org.slf4j.LoggerFactory
import org.springframework.context.MessageSource
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.HttpMediaTypeNotAcceptableException
import org.springframework.web.accept.ContentNegotiationStrategy
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.servlet.ModelAndView
import java.util.Locale

/**
 * @author zhanhb
 */
@ControllerAdvice
class BusinessExceptionHandler(
        private val messageSource: MessageSource,
        private val contentNegotiationStrategy: ContentNegotiationStrategy
) {

    @ExceptionHandler
    @Throws(HttpMediaTypeNotAcceptableException::class)
    fun handler(businessException: BusinessException, locale: Locale, nativeWebRequest: NativeWebRequest): Any {
        val code = businessException.code
        val message = code.message
        val formatted = messageSource.getMessage(message, businessException.params, message, locale)

        val mediaTypes = contentNegotiationStrategy.resolveMediaTypes(nativeWebRequest)
        log.debug("mediaTypes: {}", mediaTypes)

        for (mediaType in mediaTypes) {
            if (mediaType.isCompatibleWith(MediaType.TEXT_HTML)) {
                break
            }
            if (mediaType.isCompatibleWith(MediaType.APPLICATION_JSON)) {
                val builder = ResponseEntity.status(code.status)
                return builder.body(mapOf("message" to formatted))
            }
        }
        return ModelAndView("message", mapOf("message" to formatted), code.status)
    }

    companion object {
        private val log = LoggerFactory.getLogger(BusinessExceptionHandler::class.java)
    }
}
