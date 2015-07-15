package com.astamuse.asta4d.web.form.flow.base;

import com.astamuse.asta4d.data.annotation.ContextData;
import com.astamuse.asta4d.data.annotation.ContextDataSet;

@ContextDataSet
public class FormRenderingData {

    @ContextData(name = FormFlowConstants.FORM_FLOW_TRACE_ID)
    private String traceId;

    @ContextData(name = FormFlowConstants.FORM_FLOW_TRACE_DATA)
    private FormFlowTraceData traceData;

    @ContextData(name = FormFlowConstants.FORM_STEP_RENDER_TARGET)
    private String renderTargetStep;

    public String getTraceId() {
        return traceId;
    }

    public FormFlowTraceData getTraceData() {
        return traceData;
    }

    public String getRenderTargetStep() {
        return renderTargetStep;
    }

}