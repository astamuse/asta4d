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
package com.astamuse.asta4d.web.form.field;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import com.astamuse.asta4d.render.Renderer;
import com.astamuse.asta4d.util.collection.ListConvertUtil;
import com.astamuse.asta4d.util.collection.RowConvertor;

public abstract class SimpleFormFieldWithOptionValueRenderer extends SimpleFormFieldValueRenderer {

    protected String retrieveDisplayStringFromStoredOptionValueMap(String selector, String nonNullString) {
        OptionValueMap storedOptionMap = PrepareRenderingDataUtil.retrieveStoredDataFromContextBySelector(selector);
        if (storedOptionMap == null) {
            return nonNullString;
        }
        String value = storedOptionMap.getDisplayText(nonNullString);
        return value == null ? "" : value;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    protected List<String> convertValueToList(Object value) {
        if (value == null) {
            return new LinkedList<>();
        } else if (value.getClass().isArray()) {
            List<Object> list = Arrays.asList((Object[]) value);
            return ListConvertUtil.transform(list, new RowConvertor<Object, String>() {
                @Override
                public String convert(int rowIndex, Object obj) {
                    return getNonNullString(obj);
                }
            });
        } else if (value instanceof Iterable) {
            return ListConvertUtil.transform((Iterable) value, new RowConvertor<Object, String>() {
                @Override
                public String convert(int rowIndex, Object obj) {
                    return getNonNullString(obj);
                }
            });
        } else {
            return Arrays.asList(getNonNullString(value));
        }
    }

    @Override
    protected Renderer renderForEdit(String nonNullString) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected Renderer addAlternativeDom(String editTargetSelector, String nonNullString) {
        throw new UnsupportedOperationException();
    }
}
