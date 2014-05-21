package com.astamuse.asta4d.web.form.field;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.astamuse.asta4d.Configuration;
import com.astamuse.asta4d.render.Renderer;
import com.astamuse.asta4d.util.ClassUtil;
import com.astamuse.asta4d.util.annotation.ConvertableAnnotationRetriever;
import com.astamuse.asta4d.web.form.annotation.FormField;

public abstract class SimpleFormFieldAdditionalRenderer implements FormFieldAdditionalRenderer {

    private static final Map<String, Field> FieldCacheMap = new ConcurrentHashMap<>();

    private Field field;

    public SimpleFormFieldAdditionalRenderer(Field field) {
        this.field = field;
    }

    public SimpleFormFieldAdditionalRenderer(Class cls, String fieldName) {
        this(retrieveField(cls, fieldName));
    }

    private static final Field retrieveField(Class cls, String fieldName) {
        String cacheKey = cls.getName() + ":" + fieldName;
        Field field = FieldCacheMap.get(cacheKey);
        if (field == null) {
            List<Field> list = new ArrayList<>(ClassUtil.retrieveAllFieldsIncludeAllSuperClasses(cls));
            for (Field f : list) {
                FormField ffAnno = ConvertableAnnotationRetriever.retrieveAnnotation(FormField.class, f.getAnnotations());
                if (ffAnno == null) {
                    continue;
                } else if (f.getName().equals(fieldName)) {
                    field = f;
                    break;
                } else if (ffAnno.name().equals(fieldName)) {
                    field = f;
                    break;
                }
            }
            if (field == null) {
                throw new RuntimeException("Could not find a field named or annotated with name [" + fieldName + "]");
            }
            if (Configuration.getConfiguration().isCacheEnable()) {
                FieldCacheMap.put(cacheKey, field);
            }
        }
        return field;
    }

    @Override
    public Field targetField() {
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
