/*
 * Copyright 2012 astamuse company,Ltd.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */

package com.astamuse.asta4d.interceptor.base;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.astamuse.asta4d.util.GroupedException;

public class InterceptorUtil {

    private final static Logger logger = LoggerFactory.getLogger(InterceptorUtil.class);

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

        List<GenericInterceptor<H>> runList = new ArrayList<>();
        if (interceptorList != null) {
            runList.addAll(interceptorList);
        }
        runList.add(new ExecutorWrapping<>(executor));
        ExceptionHandler eh = new ExceptionHandler();
        GenericInterceptor<H> lastInterceptor = beforeProcess(executionHolder, runList, eh);
        afterProcess(lastInterceptor, executionHolder, runList, eh);
        // if the passed exception has not been cleared, we will throw it
        // anyway.
        if (eh.getException() != null) {
            throw eh.getException();
        }

    }

    private final static <H> GenericInterceptor<H> beforeProcess(H execution, List<GenericInterceptor<H>> interceptorList,
            ExceptionHandler eh) throws Exception {
        GenericInterceptor<H> lastInterceptor = null;
        for (GenericInterceptor<H> interceptor : interceptorList) {
            try {
                if (!interceptor.beforeProcess(execution)) {
                    break;
                }
                lastInterceptor = interceptor;
            } catch (Exception ex) {
                eh.setException(ex);
                return lastInterceptor;
            }
        }
        return lastInterceptor;
    }

    private final static <H> void afterProcess(GenericInterceptor<H> lastInterceptor, H execution,
            List<GenericInterceptor<H>> interceptorList, ExceptionHandler eh) {
        GenericInterceptor<H> interceptor = null;
        boolean foundStoppedPoint = false;
        List<Exception> exList = new LinkedList<>();
        for (int i = interceptorList.size() - 1; i >= 0; i--) {
            interceptor = interceptorList.get(i);
            if (!foundStoppedPoint) {
                foundStoppedPoint = interceptor == lastInterceptor;
            }
            if (foundStoppedPoint) {
                try {
                    interceptor.afterProcess(execution, eh);
                } catch (Exception afterEx) {
                    logger.warn("There is an exception occured in after process of interceptors.", afterEx);
                    exList.add(afterEx);
                }
            }
        }

        // if there are exceptions in after process, we should throw them.
        if (!exList.isEmpty()) {
            GroupedException ge = new GroupedException();
            ge.setExceptionList(exList);
            logger.error("There are exception(s) orrcured on after interceptor process", ge);
            throw ge;
        }
    }
}
