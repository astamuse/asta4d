/*
 * Copyright 2016 astamuse company,Ltd.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */
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