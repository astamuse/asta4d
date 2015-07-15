package com.astamuse.asta4d.web.form.flow.base;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.astamuse.asta4d.web.form.validation.FormValidationMessage;
import com.astamuse.asta4d.web.form.validation.FormValidator;
import com.astamuse.asta4d.web.form.validation.JsrValidator;
import com.astamuse.asta4d.web.form.validation.TypeUnMatchValidator;
import com.astamuse.asta4d.web.util.message.DefaultMessageRenderingHelper;

public interface ValidationProcessor {
    /**
     * Sub classes can override this method to customize how to handle the validation result
     * 
     * @param form
     * @return
     */
    default CommonFormResult processValidation(FormProcessData processData, Object form) {
        List<FormValidationMessage> validationMesssages = validate(form);
        if (validationMesssages.isEmpty()) {
            return CommonFormResult.SUCCESS;
        } else {
            for (FormValidationMessage msg : validationMesssages) {
                outputValidationMessage(msg);
            }
            return CommonFormResult.FAILED;
        }
    }

    /**
     * Sub classes can override this method to customize how to output validation messages
     * 
     * @param msg
     */
    default void outputValidationMessage(FormValidationMessage msg) {
        DefaultMessageRenderingHelper.getConfiguredInstance().err("#" + msg.getFieldName() + "-err-msg", msg.getMessage());
    }

    /**
     * 
     * Sub classes can override this method to supply customized validation mechanism.
     * 
     * @param form
     * @return
     */
    default List<FormValidationMessage> validate(Object form) {
        List<FormValidationMessage> validationMessages = new LinkedList<>();

        Set<String> fieldNameSet = new HashSet<String>();

        List<FormValidationMessage> typeMessages = getTypeUnMatchValidator().validate(form);
        for (FormValidationMessage message : typeMessages) {
            validationMessages.add(message);
            fieldNameSet.add(message.getFieldName());
        }

        List<FormValidationMessage> valueMessages = getValueValidator().validate(form);

        // there may be a not null/empty value validation error for the fields which has been validated as type unmatch, we simply remove
        // them.

        for (FormValidationMessage message : valueMessages) {
            if (!fieldNameSet.contains(message.getFieldName())) {
                validationMessages.add(message);
            }
        }

        return validationMessages;
    }

    /**
     * Sub classes can override this method to supply a customized type unmatch validator
     * 
     * @return
     */
    default FormValidator getTypeUnMatchValidator() {
        return new TypeUnMatchValidator();
    }

    /**
     * Sub classes can override this method to supply a customized value validator
     * 
     * @return
     */
    default FormValidator getValueValidator() {
        return new JsrValidator();
    }
}
