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

import org.apache.commons.lang3.StringUtils;

import com.astamuse.asta4d.util.annotation.AnnotatedPropertyInfo;
import com.astamuse.asta4d.web.form.CascadeFormUtil;
import com.astamuse.asta4d.web.form.annotation.FormField;

public abstract class CommonValidatorBase {

    protected boolean addFieldLablePrefixToMessage;

    public CommonValidatorBase() {
        this(true);
    }

    public CommonValidatorBase(boolean addFieldLablePrefixToMessage) {
        this.addFieldLablePrefixToMessage = addFieldLablePrefixToMessage;
    }

    @SuppressWarnings("rawtypes")
    protected String createAnnotatedMessage(Class formCls, String fieldName, String fieldLabel, String annotatedMsg) {
        if (addFieldLablePrefixToMessage) {
            String msgTemplate = "%s: %s";
            return String.format(msgTemplate, fieldLabel, annotatedMsg);
        } else {
            return annotatedMsg;
        }
    }

    protected String retrieveFieldName(AnnotatedPropertyInfo field, int[] indexes) {
        return CascadeFormUtil.rewriteArrayIndexPlaceHolder(field.getName(), indexes);
    }

    protected String retrieveFieldLabel(AnnotatedPropertyInfo field, int[] indexes) {
        FormField ff = field.getAnnotation(FormField.class);
        if (ff == null) {
            // impossible but
            throw new NullPointerException();
        }
        String label = ff.nameLabel();
        if (StringUtils.isEmpty(label)) {
            label = field.getName();
        }

        return CascadeFormUtil.rewriteArrayIndexPlaceHolder(label, indexes);
    }

    protected String retrieveFieldTypeName(AnnotatedPropertyInfo field) {
        return field.getType().getSimpleName();
    }

    protected String retrieveFieldAnnotatedMessage(AnnotatedPropertyInfo field) {
        FormField ff = field.getAnnotation(FormField.class);
        if (ff == null) {
            // impossible but
            return "";
        }
        return ff.message();
    }
}
