package com.astamuse.asta4d.web.form.flow.base;

import java.util.Map;

import com.astamuse.asta4d.Context;
import com.astamuse.asta4d.data.annotation.ContextData;
import com.astamuse.asta4d.data.annotation.ContextDataSet;

public @ContextDataSet class FormRenderingData {
    @ContextData(name = FormFlowConstants.FORM_STEP_TRACE_MAP)
    private Map<String, Object> traceMap;

    @ContextData(name = FormFlowConstants.FORM_STEP_TRACE_MAP_STR, scope = Context.SCOPE_DEFAULT)
    private String traceMapStr;

    @ContextData(name = FormFlowConstants.FORM_STEP_RENDER_TARGET)
    private String renderTargetStep;

    public Map<String, Object> getTraceMap() {
        return traceMap;
    }

    public String getTraceMapStr() {
        return traceMapStr;
    }

    public String getRenderTargetStep() {
        return renderTargetStep;
    }

}