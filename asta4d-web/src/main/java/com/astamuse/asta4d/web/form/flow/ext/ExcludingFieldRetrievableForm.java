package com.astamuse.asta4d.web.form.flow.ext;

public interface ExcludingFieldRetrievableForm {

    public String[] getExcludeFields();

    default void copyIncludingFieldsTo(Object targetForm) {
        ExcludingFieldRetrievableFormHelper.copyIncludeFieldsOnly(targetForm, this);
    }
}
