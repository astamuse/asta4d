package com.astamuse.asta4d.web.form.flow.ext;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.astamuse.asta4d.web.form.flow.base.ValidationProcessor;
import com.astamuse.asta4d.web.form.validation.FormValidationMessage;

public interface SimpleFormFieldExcludeValidationProcessor extends ValidationProcessor {

    default List<FormValidationMessage> postValidate(Object form, List<FormValidationMessage> msgList) {
        return filterExcludedFieldsMessages(form, msgList);
    }

    /**
     * include/exclude corresponding field messages according to the {@link SimpleFormFieldIncludeDescription}/
     * {@link SimpleFormFieldExcludeDescprition}
     * 
     * @param form
     * @param msgList
     * @return
     */
    default List<FormValidationMessage> filterExcludedFieldsMessages(Object form, List<FormValidationMessage> msgList) {
        if (form instanceof SimpleFormFieldExcludeDescprition) {
            SimpleFormFieldExcludeDescprition vfe = (SimpleFormFieldExcludeDescprition) form;
            String[] fields = vfe.getExcludeFields();
            Set<String> set = new HashSet<>();
            for (String f : fields) {
                set.add(f);
            }
            return msgList.stream().filter(fvm -> !set.contains(fvm.getFieldName())).collect(Collectors.toList());
        } else {
            return msgList;
        }
    }
}
