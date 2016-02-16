package com.astamuse.asta4d.web.form.flow.base;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.reflect.FieldUtils;

import com.astamuse.asta4d.Configuration;
import com.astamuse.asta4d.util.ClassUtil;

public class StepAwaredValidationFormHelper {

    /**
     * We use this cache as copyOnWrite, we do not mind the cache may fail in multiple thread, the content will be cached eventually at
     * sometime.
     */
    private static Map<String, Optional<Field>> ValidationTargetFieldCache = new HashMap<>();

    static final Object getValidationTargetByAnnotation(Object form, String step) {
        String cacheKey = step + "@" + form.getClass().getName();
        Optional<Field> oField = ValidationTargetFieldCache.get(cacheKey);

        if (oField == null) {
            List<Field> list = new ArrayList<>(ClassUtil.retrieveAllFieldsIncludeAllSuperClasses(form.getClass()));
            Iterator<Field> it = list.iterator();
            while (it.hasNext()) {
                Field f = it.next();
                StepAwaredValidationTarget vtAnno = f.getAnnotation(StepAwaredValidationTarget.class);
                if (vtAnno == null) {
                    continue;
                }
                String representingStep = vtAnno.value();
                if (step.equals(representingStep)) {
                    oField = Optional.of(f);
                    break;
                }
            }
            if (oField == null) {
                oField = Optional.empty();
            }
            if (Configuration.getConfiguration().isCacheEnable()) {
                Map<String, Optional<Field>> newCache = new HashMap<>(ValidationTargetFieldCache);
                newCache.put(cacheKey, oField);
                ValidationTargetFieldCache = newCache;
            }
        }

        if (oField.isPresent()) {
            try {
                return FieldUtils.readField(oField.get(), form, true);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        } else {
            return form;
        }
    }

}
