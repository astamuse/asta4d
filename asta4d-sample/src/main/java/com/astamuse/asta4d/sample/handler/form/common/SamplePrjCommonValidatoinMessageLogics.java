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

    private OrderedParamI18nMessageHelper messageHelper = I18nMessageHelperTypeAssistant.getConfiguredOrderedHelper();

    public SamplePrjCommonValidatoinMessageLogics(OrderedParamI18nMessageHelper messageHelper) {
        super();
        this.messageHelper = messageHelper;
    }

    @SuppressWarnings("rawtypes")
    public String createAnnotatedMessage(Class formCls, String fieldName, String fieldLabel, String annotatedMsg) {
        // treat the annotated message as key
        return messageHelper.getMessageWithDefault(annotatedMsg, annotatedMsg);
    }
}
