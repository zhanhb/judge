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
import org.thymeleaf.model.IProcessableElementTag
import org.thymeleaf.processor.AbstractProcessor
import org.thymeleaf.processor.element.IElementTagProcessor
import org.thymeleaf.processor.element.IElementTagStructureHandler
import org.thymeleaf.processor.element.MatchingAttributeName
import org.thymeleaf.processor.element.MatchingElementName
import org.thymeleaf.templatemode.TemplateMode

/**
 *
 * @author zhanhb
 */
internal class AttributesInnerWhitespacesProcessor(templateMode: TemplateMode, precedence: Int) : AbstractProcessor(templateMode, precedence), IElementTagProcessor {

    override fun process(context: ITemplateContext, tag: IProcessableElementTag, structureHandler: IElementTagStructureHandler) {
        val attributes = tag.allAttributes
        for (i in attributes.indices.reversed()) {
            structureHandler.removeAttribute(attributes[i].attributeDefinition.attributeName)
        }
        for (attribute in attributes) {
            structureHandler.replaceAttribute(attribute.attributeDefinition.attributeName, attribute.attributeCompleteName, attribute.value, attribute.valueQuotes)
        }
    }

    override fun getMatchingElementName(): MatchingElementName {
        return MatchingElementName.forAllElements(templateMode)
    }

    override fun getMatchingAttributeName(): MatchingAttributeName {
        return MatchingAttributeName.forAllAttributes(templateMode)
    }

}
