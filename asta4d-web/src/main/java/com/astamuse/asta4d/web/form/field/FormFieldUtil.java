package com.astamuse.asta4d.web.form.field;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.astamuse.asta4d.util.ClassUtil;
import com.astamuse.asta4d.util.annotation.ConvertableAnnotationRetriever;
import com.astamuse.asta4d.web.form.annotation.FormField;

public class FormFieldUtil {

    public static final List<Field> retrieveFormFields(Class formCls) {
        List<Field> list = new ArrayList<>(ClassUtil.retrieveAllFieldsIncludeAllSuperClasses(formCls));
        Iterator<Field> it = list.iterator();
        while (it.hasNext()) {
            Field f = it.next();
            FormField ff = ConvertableAnnotationRetriever.retrieveAnnotation(FormField.class, f.getAnnotations());
            if (ff == null) {
                it.remove();
            }
        }
        return list;
    }

    public static final void storeFieldSupportValueDataToContext(Class formCls, String clsFieldName, Object data) {

    }

    public static final void storeFieldSupportValueDataToContext(Field field, Object data) {

    }

    public static final void storeFieldSupportValueDataToContext(String formFieldName, Object data) {

    }

}
