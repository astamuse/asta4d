package com.astamuse.asta4d.web.form.validation;

import java.util.List;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import com.astamuse.asta4d.util.collection.ListConvertUtil;
import com.astamuse.asta4d.util.collection.RowConvertor;

public class JsrValidator implements FormValidator {

    protected ValidatorFactory defaultFactory = Validation.buildDefaultValidatorFactory();
    protected Validator defaultValidator = defaultFactory.getValidator();
    protected RowConvertor<ConstraintViolation<Object>, FormValidationMessage> ConstraintViolationConvertor = new RowConvertor<ConstraintViolation<Object>, FormValidationMessage>() {

        @Override
        public FormValidationMessage convert(int rowIndex, ConstraintViolation<Object> cv) {
            String fieldName = cv.getPropertyPath().toString();
            String renderMsg = fieldName + " " + cv.getMessage();

            return new FormValidationMessage(fieldName, renderMsg);
        }

    };

    @Override
    public List<FormValidationMessage> validate(Object form) {
        Set<ConstraintViolation<Object>> cvs = defaultValidator.validate(form);
        return ListConvertUtil.transform(cvs, ConstraintViolationConvertor);
    }
}
