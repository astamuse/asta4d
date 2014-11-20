package com.astamuse.asta4d.web.form.validation;

import org.apache.commons.lang3.StringUtils;

import com.astamuse.asta4d.util.annotation.AnnotatedPropertyInfo;
import com.astamuse.asta4d.web.form.CascadeFormUtil;
import com.astamuse.asta4d.web.form.annotation.FormField;

public abstract class CommonValidatorBase {

    protected boolean addFieldLablePrefixToMessage;

    public CommonValidatorBase() {
        this(true);
    }

    public CommonValidatorBase(boolean addFieldLablePrefixToMessage) {
        this.addFieldLablePrefixToMessage = addFieldLablePrefixToMessage;
    }

    @SuppressWarnings("rawtypes")
    protected String createAnnotatedMessage(Class formCls, String fieldName, String fieldLabel, String annotatedMsg) {
        if (addFieldLablePrefixToMessage) {
            String msgTemplate = "%s: %s";
            return String.format(msgTemplate, fieldLabel, annotatedMsg);
        } else {
            return annotatedMsg;
        }
    }

    protected String retrieveFieldName(AnnotatedPropertyInfo field, int arrayIndex) {
        String name = field.getName();
        if (arrayIndex >= 0) {
            name = CascadeFormUtil.rewriteArrayIndexPlaceHolder(name, arrayIndex);
        }
        return name;
    }

    protected String retrieveFieldLabel(AnnotatedPropertyInfo field, int arrayIndex) {
        FormField ff = field.getAnnotation(FormField.class);
        if (ff == null) {
            // impossible but
            throw new NullPointerException();
        }
        String label = ff.nameLabel();
        if (StringUtils.isEmpty(label)) {
            label = field.getName();
        }

        label = CascadeFormUtil.rewriteArrayIndexPlaceHolder(label, arrayIndex);
        return label;
    }

    protected String retrieveFieldTypeName(AnnotatedPropertyInfo field) {
        return field.getType().getSimpleName();
    }

    protected String retrieveFieldAnnotatedMessage(AnnotatedPropertyInfo field) {
        FormField ff = field.getAnnotation(FormField.class);
        if (ff == null) {
            // impossible but
            return "";
        }
        return ff.message();
    }
}
