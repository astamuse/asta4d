/*
 * Copyright 2014 astamuse company,Ltd.
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

import com.astamuse.asta4d.data.annotation.ContextDataSet;
import com.astamuse.asta4d.web.annotation.QueryParam;

@ContextDataSet
public class SimpleFormProcessData implements FormProcessData {

    @QueryParam(name = "step-exit")
    private String stepExit;

    @QueryParam(name = "step-current")
    private String stepCurrent;

    @QueryParam(name = "step-failed")
    private String stepFailed;

    @QueryParam(name = "step-success")
    private String stepSuccess;

    @QueryParam(name = "step-back")
    private String stepBack;

    @QueryParam(name = FormFlowConstants.FORM_FLOW_TRACE_ID_QUERY_PARAM)
    private String flowTraceId;

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

    public String getFlowTraceId() {
        return flowTraceId;
    }

    @Override
    public String getStepExit() {
        return stepExit;
    }

}