package com.astamuse.asta4d.web.form.flow.classical;

import java.util.HashSet;
import java.util.Set;

public class ClassicalMultiStepFormFlowHelper {
    /**
     * Contains the step name of confirm and complete.
     * 
     * @see ClassicalFormFlowConstant#STEP_CONFIRM
     * @see ClassicalFormFlowConstant#STEP_COMPLETE
     */
    static Set<String> NonEditSteps = new HashSet<>();
    static {
        NonEditSteps.add(ClassicalFormFlowConstant.STEP_CONFIRM);
        NonEditSteps.add(ClassicalFormFlowConstant.STEP_COMPLETE);
    }
}
