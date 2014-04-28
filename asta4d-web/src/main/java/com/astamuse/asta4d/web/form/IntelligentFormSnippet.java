package com.astamuse.asta4d.web.form;

import java.lang.reflect.Field;
import java.util.List;

import org.apache.commons.lang3.reflect.FieldUtils;

import com.astamuse.asta4d.data.annotation.ContextData;
import com.astamuse.asta4d.render.Renderer;

public class IntelligentFormSnippet<T> {

    public static final String PRE_DEFINED_FORM = "PRE_DEFINED_FORM#IntelligentFormSnippetBase";

    @ContextData(name = PRE_DEFINED_FORM)
    protected T form;

    public Renderer render() throws Exception {
        Renderer render = Renderer.create();
        if (form == null) {
            return render;
        }
        List<Field> fieldList = FormFieldUtil.retrieveFormFields(form.getClass());

        for (Field field : fieldList) {
            Object v = FieldUtils.readField(field, form, true);
            render.add("name=[" + field.getName() + "]", v);
        }
        return render;
    }
}
