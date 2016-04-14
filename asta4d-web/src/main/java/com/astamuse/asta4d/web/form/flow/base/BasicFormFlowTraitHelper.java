/*
 * Copyright 2016 astamuse company,Ltd.
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
package com.astamuse.asta4d.web.form.flow.base;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Element;

import com.astamuse.asta4d.Configuration;
import com.astamuse.asta4d.util.annotation.AnnotatedPropertyInfo;
import com.astamuse.asta4d.util.annotation.AnnotatedPropertyUtil;
import com.astamuse.asta4d.web.form.annotation.FormField;
import com.astamuse.asta4d.web.form.field.FormFieldValueRenderer;

public class BasicFormFlowTraitHelper {
    public static class FieldRenderingInfo {
        String editSelector;
        String displaySelector;
        FormFieldValueRenderer valueRenderer;

        FieldRenderingInfo replaceArrayIndex(BasicFormFlowSnippetTrait snippet, int[] indexes) {
            FieldRenderingInfo newInfo = new FieldRenderingInfo();
            newInfo.editSelector = snippet.rewriteArrayIndexPlaceHolder(editSelector, indexes);
            newInfo.displaySelector = snippet.rewriteArrayIndexPlaceHolder(displaySelector, indexes);
            newInfo.valueRenderer = valueRenderer;
            return newInfo;
        }
    }

    private static final Map<String, List<AnnotatedPropertyInfo>> RenderingTargetFieldsMap = new ConcurrentHashMap<>();

    private static final Map<AnnotatedPropertyInfo, BasicFormFlowTraitHelper.FieldRenderingInfo> FieldRenderingInfoMap = new ConcurrentHashMap<>();

    static final List<AnnotatedPropertyInfo> retrieveRenderTargetFieldList(Object form) {
        List<AnnotatedPropertyInfo> list = RenderingTargetFieldsMap.get(form.getClass().getName());
        if (list == null) {
            list = new LinkedList<AnnotatedPropertyInfo>(AnnotatedPropertyUtil.retrieveProperties(form.getClass()));
            Iterator<AnnotatedPropertyInfo> it = list.iterator();
            while (it.hasNext()) {
                // remove all the non form field properties
                if (it.next().getAnnotation(FormField.class) == null) {
                    it.remove();
                }
            }
            RenderingTargetFieldsMap.put(form.getClass().getName(), list);
        }
        return list;
    }

    static final BasicFormFlowTraitHelper.FieldRenderingInfo getRenderingInfo(BasicFormFlowSnippetTrait snippet, AnnotatedPropertyInfo f,
            int[] indexes) {
        BasicFormFlowTraitHelper.FieldRenderingInfo info = FieldRenderingInfoMap.get(f);
        if (info == null) {

            info = new BasicFormFlowTraitHelper.FieldRenderingInfo();

            FormField ffAnno = f.getAnnotation(FormField.class);

            String fieldName = f.getName();

            String editSelector = ffAnno.editSelector();
            if (StringUtils.isEmpty(editSelector)) {
                editSelector = snippet.defaultEditElementSelectorForField(fieldName);
            }

            info.editSelector = editSelector;

            String displaySelector = ffAnno.displaySelector();
            if (StringUtils.isEmpty(displaySelector)) {
                displaySelector = snippet.defaultDisplayElementSelectorForField(fieldName);
            }

            info.displaySelector = displaySelector;

            try {
                info.valueRenderer = ffAnno.fieldValueRenderer().newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }

            if (Configuration.getConfiguration().isCacheEnable()) {
                FieldRenderingInfoMap.put(f, info);
            }
        }
        if (indexes.length > 0) {
            return info.replaceArrayIndex(snippet, indexes);
        } else {
            return info;
        }
    }

    //@formatter:off
    static final String[] DefaultCascadeFormFieldArrayRefTargetAttrs = { 
        "id", 
        "name", 
        "cascade-ref",
        "cascade-ref-target",
        "cascade-ref-info-1",
        "cascade-ref-info-2",
        "cascade-ref-info-3",
        "cascade-ref-info-4",
        "cascade-ref-info-5",
        "cascade-ref-info-6",
        "cascade-ref-info-7",
        "cascade-ref-info-8",
        "cascade-ref-info-9",
    };
    //@formatter:on

    static Element ClientCascadeJsContentCache = null;
}
