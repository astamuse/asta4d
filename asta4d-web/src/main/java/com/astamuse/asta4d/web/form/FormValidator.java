package com.astamuse.asta4d.web.form;

import java.util.List;

public interface FormValidator {
    public List<FormValidationMessage> validate(Object form);
}
