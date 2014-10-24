package com.astamuse.asta4d.web.form.flow.classical;

public abstract class OneStepFormHandler<T> extends MultiStepFormFlowHandler<T> {

    public static final String VAR_INPUT_TEMPLATE_FILE = VAR_TEMPLATE_BASE_PATH;

    public OneStepFormHandler(Class<T> formCls, String inputTemplateFile) {
        super(formCls, inputTemplateFile);
    }

    public OneStepFormHandler(Class<T> formCls) {
        super(formCls);
    }

    protected boolean doUpdateOnSuccess(String step) {
        return true;
    }

    @Override
    protected boolean treatCompleteStepAsExit() {
        return true;
    }

    /**
     * for a one step form, we will always return the configured template path
     */
    protected String createTemplateFilePath(String templateBasePath, String step) {
        return templateBasePath;
    }

    protected abstract void updateForm(T form);

}
