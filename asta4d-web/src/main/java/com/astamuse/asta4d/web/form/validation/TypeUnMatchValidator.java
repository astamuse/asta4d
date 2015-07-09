/*
 * Copyright 2014 astamuse company,Ltd.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */
package com.astamuse.asta4d.web.form.validation;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import com.astamuse.asta4d.data.ContextDataHolder;
import com.astamuse.asta4d.data.InjectTrace;
import com.astamuse.asta4d.util.annotation.AnnotatedPropertyInfo;
import com.astamuse.asta4d.util.annotation.AnnotatedPropertyUtil;
import com.astamuse.asta4d.util.collection.ListConvertUtil;
import com.astamuse.asta4d.util.collection.RowConvertor;
import com.astamuse.asta4d.web.form.annotation.CascadeFormField;

public class TypeUnMatchValidator extends CommonValidatorBase implements FormValidator {

    public TypeUnMatchValidator() {
        super();
    }

    public TypeUnMatchValidator(boolean addFieldLablePrefixToMessage) {
        super(addFieldLablePrefixToMessage);
    }

    @Override
    public List<FormValidationMessage> validate(Object form) {
        List<FormValidationMessage> msgList = new LinkedList<>();
        addMessage(msgList, form, EMPTY_INDEXES);
        return msgList;
    }

    @SuppressWarnings("rawtypes")
    private void addMessage(List<FormValidationMessage> msgList, Object form, int[] indexes) {
        List<AnnotatedPropertyInfo> fieldList = AnnotatedPropertyUtil.retrieveProperties(form.getClass());

        try {
            for (AnnotatedPropertyInfo field : fieldList) {

                CascadeFormField cff = field.getAnnotation(CascadeFormField.class);
                if (cff != null) {
                    Object subform = field.retrieveValue(form);
                    if (StringUtils.isEmpty(cff.arrayLengthField())) {
                        // simple cascade form
                        addMessage(msgList, subform, indexes);
                    } else {
                        // array cascade form
                        int len = Array.getLength(subform);
                        for (int i = 0; i < len; i++) {
                            addMessage(msgList, Array.get(subform, i), ArrayUtils.add(indexes, i));
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
                    msgList.add(createTypeUnMatchMessage(form.getClass(), field, valueHolder, indexes));
                }
            }
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("rawtypes")
    protected FormValidationMessage createTypeUnMatchMessage(Class formCls, AnnotatedPropertyInfo field, ContextDataHolder valueHolder,
            int[] indexes) {
        String fieldName = retrieveFieldName(field, indexes);
        String fieldLabel = retrieveFieldLabel(field, indexes);
        String annotatedMsg = retrieveFieldAnnotatedMessage(field);

        String msg;
        if (StringUtils.isNotEmpty(annotatedMsg)) {
            msg = createAnnotatedMessage(formCls, fieldName, fieldLabel, annotatedMsg);
        } else {
            String fieldTypeName = retrieveFieldTypeName(field);
            String valueString = generateValueString(valueHolder.getFoundOriginalData(), field.getType());
            msg = createMessage(formCls, fieldName, fieldLabel, fieldTypeName, valueString);
        }
        return new FormValidationMessage(fieldName, msg);
    }

    @SuppressWarnings("rawtypes")
    protected String createMessage(Class formCls, String fieldName, String fieldLabel, String fieldTypeName, String valueString) {
        if (addFieldLablePrefixToMessage) {
            String msgTemplate = "%s: %s is expected but value[%s] found.";
            return String.format(msgTemplate, fieldLabel, fieldTypeName, valueString);
        } else {
            String msgTemplate = "%s is expected but value[%s] found.";
            return String.format(msgTemplate, fieldTypeName, valueString);
        }
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
