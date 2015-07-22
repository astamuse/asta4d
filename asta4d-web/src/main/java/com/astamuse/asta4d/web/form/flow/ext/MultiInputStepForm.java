package com.astamuse.asta4d.web.form.flow.ext;

public interface MultiInputStepForm {

    public Object getSubInputFormByStep(String step);

    public void setSubInputFormForStep(String step, Object subForm);
}
