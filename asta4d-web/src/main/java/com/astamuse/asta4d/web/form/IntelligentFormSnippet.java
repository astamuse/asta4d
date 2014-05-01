package com.astamuse.asta4d.web.form;

import java.lang.reflect.Field;
import java.util.List;

import org.apache.commons.lang3.reflect.FieldUtils;

import com.astamuse.asta4d.Context;
import com.astamuse.asta4d.data.ContextDataHolder;
import com.astamuse.asta4d.data.InjectTrace;
import com.astamuse.asta4d.data.annotation.ContextData;
import com.astamuse.asta4d.render.Renderer;
import com.astamuse.asta4d.snippet.InitializableSnippet;
import com.astamuse.asta4d.snippet.SnippetInvokeException;
import com.astamuse.asta4d.web.WebApplicationContext;

public class IntelligentFormSnippet<T> implements InitializableSnippet {

    public static final String PRE_DEFINED_FORM = "PRE_DEFINED_FORM#IntelligentFormSnippetBase";

    public static final String PRE_INJECTION_TRACE_INFO = "PRE_INJECTION_TRACE_INFO#IntelligentFormSnippetBase";

    @ContextData(name = PRE_DEFINED_FORM)
    protected T form;

    @Override
    public void init() throws SnippetInvokeException {
        List list = (List) Context.getCurrentThreadContext().getData(WebApplicationContext.SCOPE_FLASH, PRE_INJECTION_TRACE_INFO);
        InjectTrace.restoreTraceList(list);

    }

    public Renderer render() throws Exception {
        Renderer render = Renderer.create();
        if (form == null) {
            return render;
        }
        List<Field> fieldList = FormFieldUtil.retrieveFormFields(form.getClass());

        for (Field field : fieldList) {
            Object v = FieldUtils.readField(field, form, true);

            // TODO wrong rendering for null
            if (v == null) {
                ContextDataHolder valueHolder = InjectTrace.getInstanceInjectionTraceInfo(form, field);
                if (valueHolder != null) {
                    v = valueHolder.getFoundOriginalData();
                }
            }
            render.add("[name=" + field.getName() + "]", "value", String.valueOf(v));
        }
        return render;
    }

}
