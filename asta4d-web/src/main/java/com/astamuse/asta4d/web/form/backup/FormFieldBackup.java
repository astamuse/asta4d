package com.astamuse.asta4d.web.form.backup;

import com.astamuse.asta4d.Configuration;
import com.astamuse.asta4d.Context;
import com.astamuse.asta4d.data.ContextDataFinder;
import com.astamuse.asta4d.data.ContextDataHolder;
import com.astamuse.asta4d.data.DataOperationException;
import com.astamuse.asta4d.render.Renderer;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class FormFieldBackup<T> extends ContextDataHolder {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private Class<T> fieldClass;

    private T fieldValue;

    private DataOperationException fieldValueConvertException = null;

    private String msgContainerSelector = null;

    private String msgRenderingSelector = null;

    private FormFieldBackup(Class<?> superCls, Class<T> fieldCls) {
        super(superCls);
        this.fieldClass = fieldCls;
    }

    public static <T> FormFieldBackup<T> type(Class<T> cls) {
        if (cls.isArray()) {
            return new FormFieldBackup<>(String[].class, cls);
        } else {
            return new FormFieldBackup<>(String.class, cls);
        }
    }

    public FormFieldBackup<T> msgContainerSelector(String msgContainerSelector) {
        this.msgContainerSelector = msgContainerSelector;
        return this;
    }

    public FormFieldBackup<T> msgRenderingSelector(String msgRenderingSelector) {
        this.msgRenderingSelector = msgRenderingSelector;
        return this;
    }

    @Override
    public void setData(String scope, String name, Object value) {
        super.setData(scope, name, value);

        if (value == null) {
            return;
        }

        // find again to convert data type
        // we should split the data convert logic from data searching logic, but
        // now, for convenience, we just find again.
        try {
            ContextDataFinder dataFinder = Configuration.getConfiguration().getContextDataFinder();
            ContextDataHolder searchHolder = dataFinder.findDataInContext(Context.getCurrentThreadContext(), scope, name, fieldClass);
            // since we had found that value at first time above, so we will
            // believe that we must have found it.
            this.fieldValue = (T) searchHolder.getValue();
        } catch (DataOperationException e) {
            fieldValueConvertException = e;
        }
    }

    public T getFieldValue() {
        return this.fieldValue;
    }

    // validation methods for convenience
    public DataOperationException getFieldValueConvertException() {
        return this.fieldValueConvertException;
    }

    public boolean notFound() {
        return this.getValue() == null;
    }

    public boolean isEmptyString() {
        if (this.getValue() == null) {
            return false;
        } else if (this.getTypeCls().isArray()) {
            String[] sa = (String[]) this.getValue();
            for (String s : sa) {
                if (!s.isEmpty()) {
                    return false;
                }
            }
            return true;
        } else {
            return this.getValue().toString().isEmpty();
        }
    }

    public void addValidationMsg(String msg) {
        String container = this.msgContainerSelector;
        if (container == null) {
            container = "#" + this.getName() + "-msg";
        }
        addValidationMsg(container, msg);
    }

    public void addValidationMsg(String itemContainerSelector, String msg) {
        String renderSelector = this.msgRenderingSelector;
        if (renderSelector == null) {
            renderSelector = "*";
        }
        addValidationMsg(itemContainerSelector, renderSelector, msg);
    }

    public void addValidationMsg(String itemContainerSelector, String msgRenderingSelector, String msg) {
        addValidationMsg(itemContainerSelector, Renderer.create(msgRenderingSelector, msg));
    }

    public void addValidationMsg(String itemContainerSelector, Renderer msgRenderer) {
        // GlobalRenderingHelper.addRenderer(itemContainerSelector, msgRenderer);
    }
}
