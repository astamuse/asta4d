package com.astamuse.asta4d.web.form.flow.base;

import com.astamuse.asta4d.data.annotation.ContextDataSet;
import com.astamuse.asta4d.web.annotation.QueryParam;

@ContextDataSet
public class SimpleFormProcessData implements FormProcessData {

    @QueryParam(name = "step-current")
    private String stepCurrent;

    @QueryParam(name = "step-failed")
    private String stepFailed;

    @QueryParam(name = "step-success")
    private String stepSuccess;

    @QueryParam(name = "step-back")
    private String stepBack;

    @QueryParam(name = FormFlowConstants.FORM_STEP_TRACE_MAP_STR)
    private String stepTraceData;

    public SimpleFormProcessData() {
    }

    public String getStepCurrent() {
        return stepCurrent;
    }

    public String getStepFailed() {
        return stepFailed;
    }

    public String getStepSuccess() {
        return stepSuccess;
    }

    public String getStepBack() {
        return stepBack;
    }

    public String getStepTraceData() {
        return stepTraceData;
    }

}