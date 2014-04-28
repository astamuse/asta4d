package com.astamuse.asta4d.web.form;

import java.util.LinkedList;
import java.util.List;

import com.astamuse.asta4d.Context;
import com.astamuse.asta4d.data.ContextDataSetFactory;
import com.astamuse.asta4d.data.InjectUtil;
import com.astamuse.asta4d.data.annotation.ContextDataSet;
import com.astamuse.asta4d.util.annotation.ConvertableAnnotationRetriever;
import com.astamuse.asta4d.web.dispatch.request.RequestHandler;
import com.astamuse.asta4d.web.dispatch.response.provider.RedirectTargetProvider;
import com.astamuse.asta4d.web.util.message.DefaultMessageRenderingHelper;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class IntelligentFormHandler<T> {

    private Class formCls;
    private ContextDataSetFactory factory;

    private IntelligentFormHandler() {
        // this.getClass().get
        // init()
    }

    public IntelligentFormHandler(Class<T> formCls) {
        init(formCls);
    }

    private void init(Class<T> formCls) {
        this.formCls = formCls;
        ContextDataSet cds = ConvertableAnnotationRetriever.retrieveAnnotation(ContextDataSet.class, formCls.getAnnotations());
        try {
            factory = cds.factory().newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @RequestHandler
    public CommonFormResult handle() throws Exception {
        Object form = factory.createInstance(formCls);
        InjectUtil.injectToInstance(form);
        return handle((T) form);
    }

    protected FormValidator getTypeUnMatchValidator() {
        return new TypeUnMatchValidator();
    }

    protected FormValidator getValueValidator() {
        return new TypeUnMatchValidator();
    }

    protected List<FormValidationMessage> validate(T form) {
        List<FormValidationMessage> validationMessages = new LinkedList<>();

        validationMessages.addAll(getTypeUnMatchValidator().validate(form));
        if (!validationMessages.isEmpty()) {
            return validationMessages;
        }

        validationMessages.addAll(getValueValidator().validate(form));
        return validationMessages;
    }

    protected CommonFormResult handle(T form) {
        List<FormValidationMessage> validationMesssages = validate(form);
        if (validationMesssages.isEmpty()) {
            return CommonFormResult.SUCCESS;
        } else {
            DefaultMessageRenderingHelper msgHelper = DefaultMessageRenderingHelper.instance();
            for (FormValidationMessage formValidationMessage : validationMesssages) {
                msgHelper.err("name=[" + formValidationMessage.getName() + "]", formValidationMessage.getMessage());
            }
            Context.getCurrentThreadContext().setData(IntelligentFormSnippet.PRE_DEFINED_FORM, form);
            RedirectTargetProvider.addFlashScopeData(IntelligentFormSnippet.PRE_DEFINED_FORM, form);
            return CommonFormResult.FAILED;
        }
    }
}
