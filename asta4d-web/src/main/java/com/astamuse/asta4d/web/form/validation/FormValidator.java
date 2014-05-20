package com.astamuse.asta4d.web.form.validation;

import java.util.List;

public interface FormValidator {
    public List<FormValidationMessage> validate(Object form);
}
