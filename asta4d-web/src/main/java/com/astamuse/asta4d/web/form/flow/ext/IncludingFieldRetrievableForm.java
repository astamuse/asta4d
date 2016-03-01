package com.astamuse.asta4d.web.form.flow.ext;

/**
 * Currently this is simple help interface to simplify excluding declaration, thus we do not support cascaded form.We will add the full
 * support of cascade form in future.
 * 
 */
public interface IncludingFieldRetrievableForm extends ExcludingFieldRetrievableForm {
    default String[] getExcludeFields() {
        return ExcludingFieldRetrievableFormHelper.retrieveExcludingFieldsByIncluding(this.getClass(), getIncludeFields());
    }

    public String[] getIncludeFields();
}
