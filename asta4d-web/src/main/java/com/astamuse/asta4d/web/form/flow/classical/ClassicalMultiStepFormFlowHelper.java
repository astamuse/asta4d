package com.astamuse.asta4d.web.form.flow.classical;

import java.util.HashSet;
import java.util.Set;

public class ClassicalMultiStepFormFlowHelper {
    static Set<String> NonEditSteps = new HashSet<>();
    static {
        NonEditSteps.add(ClassicalFormFlowConstant.STEP_CONFIRM);
        NonEditSteps.add(ClassicalFormFlowConstant.STEP_COMPLETE);
    }
}
