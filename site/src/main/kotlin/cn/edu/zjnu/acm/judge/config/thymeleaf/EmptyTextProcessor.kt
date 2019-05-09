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
package cn.edu.zjnu.acm.judge.config.thymeleaf

import org.thymeleaf.context.ITemplateContext
import org.thymeleaf.model.IText
import org.thymeleaf.processor.text.AbstractTextProcessor
import org.thymeleaf.processor.text.ITextStructureHandler
import org.thymeleaf.templatemode.TemplateMode

/**
 *
 * @author zhanhb
 */
internal class EmptyTextProcessor(templateMode: TemplateMode, precedence: Int) : AbstractTextProcessor(templateMode, precedence) {

    public override fun doProcess(context: ITemplateContext, text: IText, structureHandler: ITextStructureHandler) {
        if (text.text.isNullOrBlank()) {
            structureHandler.removeText()
        }
    }

}
