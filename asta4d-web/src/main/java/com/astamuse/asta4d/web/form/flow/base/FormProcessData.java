package com.astamuse.asta4d.web.form.flow.base;

public interface FormProcessData {

    public abstract String getStepExit();

    public abstract String getStepCurrent();

    public abstract String getStepFailed();

    public abstract String getStepSuccess();

    public abstract String getStepBack();

    public abstract String getStepTraceData();

}