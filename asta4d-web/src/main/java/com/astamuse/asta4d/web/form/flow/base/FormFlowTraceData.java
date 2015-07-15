package com.astamuse.asta4d.web.form.flow.base;

import java.util.HashMap;
import java.util.Map;

public class FormFlowTraceData {

    private Map<String, Object> stepFormMap;

    public FormFlowTraceData() {
        stepFormMap = new HashMap<>();
    }

    public Map<String, Object> getStepFormMap() {
        return stepFormMap;
    }

}
