package com.astamuse.asta4d.sample.handler.form.common;

import javax.validation.Validation;
import javax.validation.Validator;

import com.astamuse.asta4d.util.i18n.I18nMessageHelperTypeAssistant;
import com.astamuse.asta4d.util.i18n.OrderedParamI18nMessageHelper;
import com.astamuse.asta4d.web.form.validation.JsrValidator;

public class SamplePrjValueValidator extends JsrValidator {

    // since the validator is thread-safe, we cache it as static
    // all the custom initialization of bean validation can be done here
    //@formatter:off
    private static Validator validator = Validation.byDefaultProvider()
                                                    .configure()
                                                    .messageInterpolator(
                                                        new Asta4DIntegratedResourceBundleInterpolator(
                                                            //supply a customized message file
                                                            new Asta4DResourceBundleFactoryAdapter("BeanValidationMessages")
                                                        )
                                                     )
                                                    .buildValidatorFactory()
                                                    .getValidator();

    //@formatter:on

    private OrderedParamI18nMessageHelper messageHelper = I18nMessageHelperTypeAssistant.getConfiguredOrderedHelper();

    private SamplePrjCommonValidatoinMessageLogics messageLogics = new SamplePrjCommonValidatoinMessageLogics(messageHelper);

    public SamplePrjValueValidator() {
        super(validator);
    }

    public SamplePrjValueValidator(boolean addFieldLablePrefixToMessage) {
        super(validator, addFieldLablePrefixToMessage);
    }

    @SuppressWarnings("rawtypes")
    @Override
    protected String createAnnotatedMessage(Class formCls, String fieldName, String fieldLabel, String annotatedMsg) {
        return messageLogics.createAnnotatedMessage(formCls, fieldName, fieldLabel, annotatedMsg);
    }
}
