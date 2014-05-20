package com.astamuse.asta4d.web.form.intelligent;

public interface IntelligentFormProcessData {

    public abstract String getStepCurrent();

    public abstract String getStepFailed();

    public abstract String getStepSuccess();

    public abstract String getStepBack();

    public abstract String getStepTraceData();

}