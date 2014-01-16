package com.astamuse.asta4d.web.form;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;

import com.astamuse.asta4d.data.annotation.ContextDataSet;

@ContextDataSet
public abstract class AbstractValidationForm {

    public AbstractValidationForm() {
        super();
    }

    public boolean isValid() {
        return isValid(retrieveValidationFieldList());
    }

    protected abstract boolean isValid(List<Field> fieldList);

    protected List<Field> retrieveValidationFieldList() {
        // ClassUtils.get
        // classu
        return Collections.emptyList();
    }

    public void addMessage(String name, String message) {
        System.err.println("name=" + name + ",[" + message + "]");
    }

}
