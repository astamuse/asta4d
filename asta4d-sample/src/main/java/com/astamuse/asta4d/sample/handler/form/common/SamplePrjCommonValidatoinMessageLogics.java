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
