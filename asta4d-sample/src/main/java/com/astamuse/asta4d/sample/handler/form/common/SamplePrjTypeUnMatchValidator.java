package com.astamuse.asta4d.sample.handler.form.common;

import com.astamuse.asta4d.util.i18n.I18nMessageHelperTypeAssistant;
import com.astamuse.asta4d.util.i18n.OrderedParamI18nMessageHelper;
import com.astamuse.asta4d.web.form.validation.TypeUnMatchValidator;

public class SamplePrjTypeUnMatchValidator extends TypeUnMatchValidator {

    private OrderedParamI18nMessageHelper messageHelper = I18nMessageHelperTypeAssistant.getConfiguredOrderedHelper();

    private SamplePrjCommonValidatoinMessageLogics messageLogics = new SamplePrjCommonValidatoinMessageLogics(messageHelper);

    public SamplePrjTypeUnMatchValidator() {
        super();
    }

    public SamplePrjTypeUnMatchValidator(boolean addFieldLablePrefixToMessage) {
        super(addFieldLablePrefixToMessage);
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

    @SuppressWarnings("rawtypes")
    @Override
    protected String createAnnotatedMessage(Class formCls, String fieldName, String fieldLabel, String annotatedMsg) {
        return messageLogics.createAnnotatedMessage(formCls, fieldName, fieldLabel, annotatedMsg);
    }

}
