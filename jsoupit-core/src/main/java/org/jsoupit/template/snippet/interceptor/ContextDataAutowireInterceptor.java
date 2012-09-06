package org.jsoupit.template.snippet.interceptor;

import org.jsoupit.Context;
import org.jsoupit.data.DataOperationException;
import org.jsoupit.data.InjectUtil;
import org.jsoupit.template.snippet.SnippetExecutionHolder;
import org.jsoupit.template.snippet.SnippetInvokeException;

public class ContextDataAutowireInterceptor implements SnippetInterceptor {

    private final static String InstanceCacheKey = ContextDataAutowireInterceptor.class + "##InstanceCacheKey##";

    @Override
    public void beforeSnippet(SnippetExecutionHolder execution) throws SnippetInvokeException {

        try {
            Context context = Context.getCurrentThreadContext();
            Object lastSnippetInstance = context.getData(InstanceCacheKey);
            // it means that we need an instance injection and a reverse
            // injection
            if (lastSnippetInstance != execution.getInstance()) {
                if (lastSnippetInstance != null) {
                    InjectUtil.setContextDataFromInstance(lastSnippetInstance);
                }
                InjectUtil.injectToInstance(execution.getInstance());
                context.setData(InstanceCacheKey, execution.getInstance());
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
