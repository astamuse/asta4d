package com.astamuse.asta4d.web.form;

import com.astamuse.asta4d.data.ContextDataHolder;

public class FormField<T> extends ContextDataHolder<T> {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private FormField(Class<T> cls) {
        super(cls);
    }

    public static <T> FormField<T> type(Class<T> cls) {
        return new FormField<>(cls);
    }
}
