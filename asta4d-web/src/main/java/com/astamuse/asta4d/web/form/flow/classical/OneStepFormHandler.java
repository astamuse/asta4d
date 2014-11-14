package com.astamuse.asta4d.web.form.flow.classical;

import java.util.Map;

import com.astamuse.asta4d.web.form.flow.base.AbstractFormFlowHandler;
import com.astamuse.asta4d.web.form.flow.base.FormProcessData;

public abstract class OneStepFormHandler<T> extends MultiStepFormFlowHandler<T> {

    public static final String VAR_INPUT_TEMPLATE_FILE = VAR_TEMPLATE_BASE_PATH;

    public OneStepFormHandler(Class<T> formCls, String inputTemplateFile) {
        super(formCls, inputTemplateFile);
    }

    public OneStepFormHandler(Class<T> formCls) {
        super(formCls);
    }

    /**
     * for a one step form, we will always do update after validation succeed.
     */
    @Override
    protected boolean doUpdateOnValidationSuccess(FormProcessData processData) {
        return true;
    }

    /**
     * for a one step form, we will always treat the complete step as exit
     */
    @Override
    protected boolean treatCompleteStepAsExit() {
        return true;
    }

    /**
     * In the parent class {@link AbstractFormFlowHandler}'s implementation of skipSaveTraceMap, it says that the sub class have the
     * responsibility to make sure save the trace map well, thus we override it to perform the obligation.
     * 
     * <p>
     * 
     * The trace map will never be saved for a one step form since there is no necessary to keep the trace
     * 
     * @param currentStep
     * @param renderTargetStep
     * @param traceMap
     * @return
     */
    protected boolean skipSaveTraceMap(String currentStep, String renderTargetStep, Map<String, Object> traceMap) {
        return true;
    }

    /**
     * for a one step form, we will always return the configured template path
     */
    protected String createTemplateFilePath(String templateBasePath, String step) {
        return templateBasePath;
    }

}
