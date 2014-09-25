package com.astamuse.asta4d.web.form.validation;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.ElementKind;
import javax.validation.Path;
import javax.validation.Path.Node;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.apache.commons.lang3.StringUtils;

import com.astamuse.asta4d.util.annotation.AnnotatedPropertyInfo;
import com.astamuse.asta4d.util.annotation.AnnotatedPropertyUtil;
import com.astamuse.asta4d.util.collection.ListConvertUtil;
import com.astamuse.asta4d.util.collection.RowConvertor;
import com.astamuse.asta4d.web.form.CascadeFormUtil;
import com.astamuse.asta4d.web.form.annotation.CascadeFormField;

public class JsrValidator implements FormValidator {

    protected Validator validator;

    public JsrValidator() {
        this(Validation.buildDefaultValidatorFactory());
    }

    public JsrValidator(ValidatorFactory factory) {
        this(factory.getValidator());
    }

    public JsrValidator(Validator validator) {
        this.validator = validator;
    }

    @Override
    public List<FormValidationMessage> validate(final Object form) {
        Set<ConstraintViolation<Object>> cvs = validator.validate(form);
        return ListConvertUtil.transform(cvs, new RowConvertor<ConstraintViolation<Object>, FormValidationMessage>() {

            @Override
            public FormValidationMessage convert(int rowIndex, ConstraintViolation<Object> cv) {
                String fieldName = convertFieldName(form.getClass(), cv.getPropertyPath());
                String fieldDisplayName = retrieveFieldDisplayName(fieldName);
                String renderMsg = fieldDisplayName + " " + cv.getMessage();

                return new FormValidationMessage(fieldName, renderMsg);
            }
        });
    }

    protected String convertFieldName(Class formCls, Path path) {
        Iterator<Node> it = path.iterator();
        Class cls = formCls;
        try {
            while (it.hasNext()) {
                Node node = it.next();
                if (node.getKind() != ElementKind.PROPERTY) {
                    // we cannot handle this case
                    return path.toString();
                }
                String name = node.getName();
                System.err.println(node.getName() + ":" + node.getKind());
                if (it.hasNext()) {// not the last
                    AnnotatedPropertyInfo field = AnnotatedPropertyUtil.retrievePropertyByBeanPropertyName(cls, name);
                    CascadeFormField cff = field.getAnnotation(CascadeFormField.class);
                    if (cff == null) {
                        // regular fields
                        cls = field.getType();
                    } else if (StringUtils.isEmpty(cff.arrayLengthField())) {
                        // simple cascading
                        cls = field.getType();
                    } else {
                        // array cascading
                        cls = field.getType().getComponentType();
                    }

                    continue;
                } else {// the last
                    AnnotatedPropertyInfo field = AnnotatedPropertyUtil.retrievePropertyByBeanPropertyName(cls, name);
                    if (field == null) {
                        // it seems we got a unexpected error
                        return path.toString();
                    } else {
                        if (node.getIndex() == null) {
                            // regular fields or simple cascading
                            return field.getName();
                        } else {
                            return CascadeFormUtil.rewriteArrayIndexPlaceHolder(field.getName(), node.getIndex());
                        }
                    }
                }
            }
            // it seems impossible
            return path.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected String retrieveFieldDisplayName(String fieldName) {
        return fieldName;
    }

}
