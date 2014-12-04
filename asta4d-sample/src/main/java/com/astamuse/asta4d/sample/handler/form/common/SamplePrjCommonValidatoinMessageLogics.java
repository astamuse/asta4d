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

/**
 * 
 * We use a splitted message logics class to share all the common logics
 * 
 * @author e-ryu
 * 
 */
public class SamplePrjCommonValidatoinMessageLogics {

    protected OrderedParamI18nMessageHelper messageHelper = I18nMessageHelperTypeAssistant.getConfiguredOrderedHelper();

    protected boolean addFieldLablePrefixToMessage;

    public SamplePrjCommonValidatoinMessageLogics(OrderedParamI18nMessageHelper messageHelper, boolean addFieldLablePrefixToMessage) {
        super();
        this.messageHelper = messageHelper;
        this.addFieldLablePrefixToMessage = addFieldLablePrefixToMessage;
    }

    // @ShowCode:showCreateAnnotatedMessageStart
    /**
     * we share the annotated message logic here
     */
    @SuppressWarnings("rawtypes")
    public String createAnnotatedMessage(Class formCls, String fieldName, String fieldLabel, String annotatedMsg) {
        // treat the annotated message as key
        String msg = messageHelper.getMessageWithDefault(annotatedMsg, annotatedMsg);
        if (addFieldLablePrefixToMessage) {
            return String.format("%s: %s", fieldLabel, msg);
        } else {
            return msg;
        }
    }
    // @ShowCode:showCreateAnnotatedMessageEnd
}
