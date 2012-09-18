package com.astamuse.asta4d.interceptor.base;

import java.util.ArrayList;
import java.util.List;

public class InterceptorUtil {

    private final static class ExecutorWrapping<H> implements GenericInterceptor<H> {

        private Executor<H> executor;

        public ExecutorWrapping(Executor<H> executor) {
            this.executor = executor;
        }

        @Override
        public boolean beforeProcess(H executionHolder) throws Exception {
            executor.execute(executionHolder);
            return true;
        }

        @Override
        public void afterProcess(H exectionHolder, ExceptionHandler ex) {
        }
    }

    public final static <H> void executeWithConvertableInterceptors(H executionHolder,
            List<GenericInterceptorConvertable<H>> interceptorList, Executor<H> executor) throws Exception {
        List<GenericInterceptor<H>> runList = new ArrayList<>();
        if (interceptorList != null) {
            for (GenericInterceptorConvertable<H> interceptor : interceptorList) {
                runList.add(interceptor.asGenericInterceptor());
            }
        }
    }

    public final static <H> void executeWithInterceptors(H executionHolder, List<? extends GenericInterceptor<H>> interceptorList,
            Executor<H> executor) throws Exception {

        GenericInterceptor<H> lastInterceptor = null;
        List<GenericInterceptor<H>> runList = new ArrayList<>();
        if (interceptorList != null) {
            runList.addAll(interceptorList);
        }
        runList.add(new ExecutorWrapping<>(executor));
        Exception executeException = null;
        try {
            lastInterceptor = beforeProcess(executionHolder, runList);
        } catch (Exception ex) {
            executeException = ex;
        }
        ExceptionHandler eh = new ExceptionHandler(executeException);
        afterProcess(lastInterceptor, executionHolder, runList, eh);
        // if the passed exception has not been cleared, we will throw it
        // anyway.
        if (eh.getException() != null) {
            throw eh.getException();
        }

    }

    private final static <H> GenericInterceptor<H> beforeProcess(H execution, List<GenericInterceptor<H>> interceptorList) throws Exception {
        GenericInterceptor<H> lastInterceptor = null;
        for (GenericInterceptor<H> interceptor : interceptorList) {
            lastInterceptor = interceptor;
            if (interceptor.beforeProcess(execution)) {
                continue;
            }
        }
        return lastInterceptor;
    }

    private final static <H> void afterProcess(GenericInterceptor<H> lastInterceptor, H execution,
            List<GenericInterceptor<H>> interceptorList, ExceptionHandler eh) {
        GenericInterceptor<H> interceptor = null;
        boolean foundStoppedPoint = false;
        for (int i = interceptorList.size() - 1; i >= 0; i--) {
            interceptor = interceptorList.get(i);
            if (!foundStoppedPoint) {
                foundStoppedPoint = interceptor == lastInterceptor;
            }
            if (foundStoppedPoint) {
                try {
                    interceptor.afterProcess(execution, eh);
                } catch (Exception afterEx) {
                    // TODO log error
                }
            }
        }
    }
}
