package com.astamuse.asta4d.web.form.validation;

public class FormValidationMessage {
    private String fieldName;
    private String message;

    public FormValidationMessage(String fieldName, String message) {
        super();
        this.fieldName = fieldName;
        this.message = message;
    }

    public String getFieldName() {
        return fieldName;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "FormValidationMessage:name=[" + fieldName + "], message=[" + message + "]";
    }

}
