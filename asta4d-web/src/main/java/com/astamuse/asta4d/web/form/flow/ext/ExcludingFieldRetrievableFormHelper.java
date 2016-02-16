package com.astamuse.asta4d.web.form.flow.ext;

import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.astamuse.asta4d.util.annotation.AnnotatedPropertyInfo;
import com.astamuse.asta4d.util.annotation.AnnotatedPropertyUtil;

public class ExcludingFieldRetrievableFormHelper {

    public static final void copyIncludeFieldsOnly(Object targetForm, ExcludingFieldRetrievableForm... froms) {
        try {
            List<AnnotatedPropertyInfo> toProps = AnnotatedPropertyUtil.retrieveProperties(targetForm.getClass());
            Map<String, AnnotatedPropertyInfo> toPropsMap = toProps.stream().collect(Collectors.toMap(p -> p.getName(), p -> p));

            for (ExcludingFieldRetrievableForm from : froms) {
                List<AnnotatedPropertyInfo> fromProps = AnnotatedPropertyUtil.retrieveProperties(from.getClass());
                String[] excludes = from.getExcludeFields();
                Set<String> set = new HashSet<>();
                for (String s : excludes) {
                    set.add(s);
                }
                for (AnnotatedPropertyInfo p : fromProps) {
                    // do not copy excluded fields
                    if (set.contains(p.getName())) {
                        continue;
                    }
                    toPropsMap.get(p.getName()).assignValue(targetForm, p.retrieveValue(from));
                }

            }
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }

    }
}
