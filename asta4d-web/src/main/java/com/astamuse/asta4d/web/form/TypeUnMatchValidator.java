package com.astamuse.asta4d.web.form;

import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;

import com.astamuse.asta4d.data.ContextDataHolder;
import com.astamuse.asta4d.data.InjectTrace;

public class TypeUnMatchValidator implements FormValidator {

    @Override
    public List<FormValidationMessage> validate(Object form) {
        List<Field> fieldList = FormFieldUtil.retrieveFormFields(form.getClass());
        List<FormValidationMessage> msgList = new LinkedList<>();
        for (Field field : fieldList) {
            ContextDataHolder valueHolder = InjectTrace.getInstanceInjectionTraceInfo(form, field);
            if (valueHolder != null) {
                msgList.add(createTypeUnMatchMessage(valueHolder));
            }
        }
        return msgList;
    }

    protected FormValidationMessage createTypeUnMatchMessage(ContextDataHolder valueHolder) {
        String originalValue = valueHolder.getFoundOriginalData().toString();
        String msg = createTypeUnMatchMessage(originalValue, valueHolder.getTypeCls());
        return new FormValidationMessage(valueHolder.getName(), msg);
    }

    protected String createTypeUnMatchMessage(String originalValue, Class targetType) {
        String msg = "%s is expected but value[%s] found.";
        msg = String.format(msg, targetType.getSimpleName(), originalValue);
        return msg;
    }

}
