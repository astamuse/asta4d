package org.jsoupit.template.snippet.interceptor;

import org.jsoupit.template.Context;
import org.jsoupit.template.data.DataOperationException;
import org.jsoupit.template.data.InjectUtil;
import org.jsoupit.template.snippet.SnippetExecutionHolder;
import org.jsoupit.template.snippet.SnippetInvokeException;

public class ContextDataAutowireInterceptor implements SnippetInterceptor {

    private final static String InstanceCacheKey = ContextDataAutowireInterceptor.class + "##InstanceCacheKey##";

    // TODO reverse wire to Context
    @Override
    public void beforeSnippet(SnippetExecutionHolder execution) throws SnippetInvokeException {
        // instance.getClass().get
        /*
        List<Object> cachedInstanceList = getCachedSnippetInstanceList();
        boolean found = false;
        for (Object object : cachedInstanceList) {
            found = object == instance;
            if (found)
                break;
        }
        */
        // TODO reverse

        try {
            Context context = Context.getCurrentThreadContext();
            Object lastSnippetInstance = context.getData(InstanceCacheKey);
            // it means that we need an instance injection and a reverse
            // injection
            if (lastSnippetInstance != execution.getInstance()) {
                // TODO need a reverse injection
                InjectUtil.injectToInstance(execution.getInstance());
            }

            Object[] params = InjectUtil.getMethodInjectParams(execution.getMethod());
            execution.setParams(params);

        } catch (DataOperationException e) {
            throw new SnippetInvokeException(e);
        }

    }

    private void fillMethodParams(SnippetExecutionHolder execution) {

    }

    @Override
    public void afterSnippet(SnippetExecutionHolder execution) {
        // TODO reverse wiring

    }

}
