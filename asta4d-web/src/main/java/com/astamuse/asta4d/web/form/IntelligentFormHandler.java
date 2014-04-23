package com.astamuse.asta4d.web.form;

import com.astamuse.asta4d.data.ContextDataSetFactory;
import com.astamuse.asta4d.data.InjectUtil;
import com.astamuse.asta4d.data.annotation.ContextDataSet;
import com.astamuse.asta4d.util.annotation.ConvertableAnnotationRetriever;
import com.astamuse.asta4d.web.dispatch.request.RequestHandler;

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

    protected CommonFormResult handle(T form) {
        return null;
    }
}
