/*
 * Copyright 2014 astamuse company,Ltd.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */
package com.astamuse.asta4d.sample.handler.form.common;

import com.astamuse.asta4d.util.i18n.I18nMessageHelperTypeAssistant;
import com.astamuse.asta4d.util.i18n.OrderedParamI18nMessageHelper;
import com.astamuse.asta4d.web.form.validation.TypeUnMatchValidator;

//@ShowCode:showSamplePrjTypeUnMatchValidatorStart
public class SamplePrjTypeUnMatchValidator extends TypeUnMatchValidator {

    private OrderedParamI18nMessageHelper messageHelper = I18nMessageHelperTypeAssistant.getConfiguredOrderedHelper();

    private SamplePrjCommonValidatoinMessageLogics messageLogics;

    public SamplePrjTypeUnMatchValidator() {
        this(true);
    }

    public SamplePrjTypeUnMatchValidator(boolean addFieldLablePrefixToMessage) {
        super(addFieldLablePrefixToMessage);
        messageLogics = new SamplePrjCommonValidatoinMessageLogics(messageHelper, addFieldLablePrefixToMessage);
    }

    @Override
    protected String createMessage(Class formCls, String fieldName, String fieldLabel, String fieldTypeName, String valueString) {

        // try to retrieve the type name by validation.type.name.[fieldTypeName]
        String typeName = messageHelper.getMessageWithDefault("validation.type.name." + fieldTypeName, fieldTypeName);

        // there is a common message for type unmatch error which requires type name and value string
        String msg = messageHelper.getMessage("validation.type.msg", typeName, valueString);

        if (addFieldLablePrefixToMessage) {
            // treat the field label as msg key of field as validation.form.[form class name].[fieldLabel]
            String formSimpleName = retrieveSimpleFormName(formCls);
            String fieldDisplayLabel = messageHelper.getMessageWithDefault("validation.form." + formSimpleName + "." + fieldLabel,
                    fieldLabel);
            msg = fieldDisplayLabel + ": " + msg;
        }

        return msg;
    }

    @SuppressWarnings("rawtypes")
    private String retrieveSimpleFormName(Class formCls) {
        String clsName = formCls.getName();
        return clsName.substring("com.astamuse.asta4d.sample.handler.form.".length());
    }

    /**
     * we override this method to treat the annotated message as a key, and note that the annotated message will be used in priority if
     * there is one specified by form field annotation
     */
    @SuppressWarnings("rawtypes")
    @Override
    protected String createAnnotatedMessage(Class formCls, String fieldName, String fieldLabel, String annotatedMsg) {
        return messageLogics.createAnnotatedMessage(formCls, fieldName, fieldLabel, annotatedMsg);
    }

}
// @ShowCode:showSamplePrjTypeUnMatchValidatorEnd