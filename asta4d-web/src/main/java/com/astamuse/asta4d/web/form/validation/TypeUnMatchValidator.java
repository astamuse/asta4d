package com.astamuse.asta4d.web.form.validation;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.astamuse.asta4d.data.ContextDataHolder;
import com.astamuse.asta4d.data.InjectTrace;
import com.astamuse.asta4d.util.collection.ListConvertUtil;
import com.astamuse.asta4d.util.collection.RowConvertor;
import com.astamuse.asta4d.web.form.field.FormFieldUtil;

public class TypeUnMatchValidator implements FormValidator {

    @Override
    public List<FormValidationMessage> validate(Object form) {
        List<Field> fieldList = FormFieldUtil.retrieveFormFields(form.getClass());
        List<FormValidationMessage> msgList = new LinkedList<>();
        for (Field field : fieldList) {
            ContextDataHolder valueHolder = InjectTrace.getInstanceInjectionTraceInfo(form, field);
            if (valueHolder != null) {
                msgList.add(createTypeUnMatchMessage(field, valueHolder));
            }
        }
        return msgList;
    }

    protected FormValidationMessage createTypeUnMatchMessage(Field field, ContextDataHolder valueHolder) {
        String msgTemplate = retrieveTypeUnMatchMessageTemplate();
        String fieldName = retrieveFieldName(field);
        String targetTypeName = retrieveTargetTypeName(field.getType());
        String valueString = generateValueString(valueHolder.getFoundOriginalData(), field.getType());

        String msg = String.format(msgTemplate, fieldName, targetTypeName, valueString);
        return new FormValidationMessage(valueHolder.getName(), msg);
    }

    protected String retrieveTypeUnMatchMessageTemplate() {
        return "%s is expecting %s but value[%s] found.";
    }

    protected String retrieveFieldName(Field field) {
        return field.getName();
    }

    protected String retrieveTargetTypeName(Class targetType) {
        return targetType.getSimpleName();
    }

    protected String generateValueString(Object originalValue, Class targetType) {
        String valueString;
        boolean originalTypeIsArray = originalValue.getClass().isArray();
        boolean targetTypeIsArray = targetType.isArray();

        if (originalTypeIsArray && targetTypeIsArray) {
            valueString = createOriginalValueString(Arrays.asList((Object[]) originalValue));
        } else if (originalTypeIsArray) {
            valueString = createOriginalValueString(Arrays.asList((Object[]) originalValue));
        } else {
            valueString = createSingleOriginalValueString(originalValue);
        }
        return valueString;
    }

    protected String createOriginalValueString(List<Object> valueList) {
        List<String> list = ListConvertUtil.transform(valueList, new RowConvertor<Object, String>() {
            @Override
            public String convert(int rowIndex, Object obj) {
                return createSingleOriginalValueString(obj);
            }
        });
        return StringUtils.join(list, ",");
    }

    protected String createSingleOriginalValueString(Object value) {
        if (value == null) {
            // impossible?
            return "null";
        } else {
            return value.toString();
        }
    }

}
