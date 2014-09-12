package com.astamuse.asta4d.web.form.field;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.astamuse.asta4d.Configuration;
import com.astamuse.asta4d.data.DataOperationException;
import com.astamuse.asta4d.render.Renderer;
import com.astamuse.asta4d.util.annotation.AnnotatedPropertyInfo;
import com.astamuse.asta4d.web.form.annotation.FormField;

public abstract class SimpleFormFieldAdditionalRenderer implements FormFieldAdditionalRenderer {

    private static final Map<String, AnnotatedPropertyInfo<FormField>> FieldCacheMap = new ConcurrentHashMap<>();

    private AnnotatedPropertyInfo<FormField> field;

    public SimpleFormFieldAdditionalRenderer(AnnotatedPropertyInfo<FormField> field) {
        this.field = field;
    }

    public SimpleFormFieldAdditionalRenderer(Class cls, String fieldName) {
        this(retrieveField(cls, fieldName));
    }

    private static final AnnotatedPropertyInfo<FormField> retrieveField(Class cls, String fieldName) {
        String cacheKey = cls.getName() + ":" + fieldName;
        AnnotatedPropertyInfo<FormField> field = FieldCacheMap.get(cacheKey);
        if (field == null) {
            List<AnnotatedPropertyInfo<FormField>> list;
            try {
                list = FormFieldUtil.retrieveFormFields(cls);
            } catch (DataOperationException e) {
                throw new RuntimeException(e);
            }
            for (AnnotatedPropertyInfo<FormField> p : list) {
                if (fieldName.equals(p.getName())) {
                    field = p;
                    break;
                } else if (p.getField() != null && p.getField().getName().equals(fieldName)) {
                    field = p;
                    break;
                }
            }
            if (field == null) {
                throw new RuntimeException("Could not find a form field named or annotated with name [" + fieldName + "]");
            }
            if (Configuration.getConfiguration().isCacheEnable()) {
                FieldCacheMap.put(cacheKey, field);
            }
        }
        return field;
    }

    @Override
    public AnnotatedPropertyInfo<FormField> targetField() {
        return field;
    }

    @Override
    public Renderer preRender(String editSelector, String displaySelector) {
        return Renderer.create();
    }

    @Override
    public Renderer postRender(String editSelector, String displaySelector) {
        return Renderer.create();
    }

}
