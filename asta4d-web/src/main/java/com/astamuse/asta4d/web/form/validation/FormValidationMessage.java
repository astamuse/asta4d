package com.astamuse.asta4d.web.form.validation;

public class FormValidationMessage {
    private String name;
    private String message;

    public FormValidationMessage(String name, String message) {
        super();
        this.name = name;
        this.message = message;
    }

    public String getName() {
        return name;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "FormValidationMessage:name=[" + name + "], message=[" + message + "]";
    }

}
