package com.astamuse.asta4d.web.form.flow.base;

import java.io.Serializable;
import java.util.List;

import com.astamuse.asta4d.Context;
import com.astamuse.asta4d.data.InjectTrace;
import com.astamuse.asta4d.web.WebApplicationContext;
import com.astamuse.asta4d.web.dispatch.RedirectInterceptor;
import com.astamuse.asta4d.web.dispatch.RedirectUtil;

public class InjectionTraceDataRedirectInterceptor implements RedirectInterceptor, Serializable {

    private static final String PRE_INJECTION_TRACE_INFO = "PRE_INJECTION_TRACE_INFO#" +
            InjectionTraceDataRedirectInterceptor.class.getName();

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    @Override
    public void beforeRedirect() {
        RedirectUtil.addFlashScopeData(PRE_INJECTION_TRACE_INFO, InjectTrace.retrieveTraceList());
    }

    @Override
    public void afterRedirectDataRestore() {
        List list = (List) Context.getCurrentThreadContext().getData(WebApplicationContext.SCOPE_FLASH, PRE_INJECTION_TRACE_INFO);
        InjectTrace.restoreTraceList(list);

    }

}
