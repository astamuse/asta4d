package com.astamuse.asta4d.web.form.validation;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.astamuse.asta4d.data.ContextDataHolder;
import com.astamuse.asta4d.data.InjectTrace;
import com.astamuse.asta4d.util.annotation.AnnotatedPropertyInfo;
import com.astamuse.asta4d.util.annotation.AnnotatedPropertyUtil;
import com.astamuse.asta4d.util.collection.ListConvertUtil;
import com.astamuse.asta4d.util.collection.RowConvertor;
import com.astamuse.asta4d.web.form.CascadeFormUtil;
import com.astamuse.asta4d.web.form.annotation.CascadeFormField;

public class TypeUnMatchValidator implements FormValidator {

    @Override
    public List<FormValidationMessage> validate(Object form) {
        List<FormValidationMessage> msgList = new LinkedList<>();
        addMessage(msgList, form, -1);
        return msgList;
    }

    @SuppressWarnings("rawtypes")
    private void addMessage(List<FormValidationMessage> msgList, Object form, int arrayIndex) {
        List<AnnotatedPropertyInfo> fieldList = AnnotatedPropertyUtil.retrieveProperties(form.getClass());

        try {
            for (AnnotatedPropertyInfo field : fieldList) {

                CascadeFormField cff = field.getAnnotation(CascadeFormField.class);
                if (cff != null) {
                    Object subform = field.retrieveValue(form);
                    if (StringUtils.isEmpty(cff.arrayLengthField())) {
                        // simple cascade form
                        addMessage(msgList, subform, -1);
                    } else {
                        // array cascade form
                        int len = Array.getLength(subform);
                        for (int i = 0; i < len; i++) {
                            addMessage(msgList, Array.get(subform, i), i);
                        }
                    }
                    continue;
                }

                ContextDataHolder valueHolder;
                if (field.getField() != null) {
                    valueHolder = InjectTrace.getInstanceInjectionTraceInfo(form, field.getField());
                } else {
                    valueHolder = InjectTrace.getInstanceInjectionTraceInfo(form, field.getSetter());
                }
                if (valueHolder != null) {
                    msgList.add(createTypeUnMatchMessage(field, valueHolder, arrayIndex));
                }
            }
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("rawtypes")
    protected FormValidationMessage createTypeUnMatchMessage(AnnotatedPropertyInfo field, ContextDataHolder valueHolder, int arrayIndex) {
        String msgTemplate = retrieveTypeUnMatchMessageTemplate();
        String fieldName = retrieveFieldName(field, arrayIndex);
        String fieldDisplayName = retrieveFieldDisplayName(fieldName);
        String targetTypeName = retrieveTargetTypeName(field.getType());
        String valueString = generateValueString(valueHolder.getFoundOriginalData(), field.getType());

        String msg = String.format(msgTemplate, fieldDisplayName, targetTypeName, valueString);
        return new FormValidationMessage(fieldName, msg);
    }

    protected String retrieveTypeUnMatchMessageTemplate() {
        return "%s is expecting %s but value[%s] found.";
    }

    protected String retrieveFieldName(AnnotatedPropertyInfo field, int arrayIndex) {
        String name = field.getName();
        if (arrayIndex >= 0) {
            name = CascadeFormUtil.rewriteArrayIndexPlaceHolder(name, arrayIndex);
        }
        return name;
    }

    protected String retrieveFieldDisplayName(String fieldName) {
        return fieldName;
    }

    @SuppressWarnings("rawtypes")
    protected String retrieveTargetTypeName(Class targetType) {
        return targetType.getSimpleName();
    }

    @SuppressWarnings("rawtypes")
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
