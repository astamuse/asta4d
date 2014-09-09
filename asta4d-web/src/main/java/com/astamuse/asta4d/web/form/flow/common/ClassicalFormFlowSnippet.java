package com.astamuse.asta4d.web.form.flow.common;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import com.astamuse.asta4d.web.form.flow.base.AbstractFormFlowSnippet;

public abstract class ClassicalFormFlowSnippet extends AbstractFormFlowSnippet {

    protected static Set<String> NonEditSteps = new HashSet<>();
    static {
        NonEditSteps.add(ClassicalFormFlowConstant.STEP_CONFIRM);
        NonEditSteps.add(ClassicalFormFlowConstant.STEP_COMPLETE);
    }

    @Override
    protected boolean renderForEdit(String step, String fieldName) {
        if (StringUtils.isEmpty(step)) {
            return true;
        } else {
            return !NonEditSteps.contains(step.toLowerCase());
        }
    }
}
