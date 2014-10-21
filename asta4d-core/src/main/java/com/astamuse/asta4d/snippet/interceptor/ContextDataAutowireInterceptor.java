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

package com.astamuse.asta4d.snippet.interceptor;

import com.astamuse.asta4d.data.DataOperationException;
import com.astamuse.asta4d.data.InjectUtil;
import com.astamuse.asta4d.interceptor.base.ExceptionHandler;
import com.astamuse.asta4d.snippet.SnippetExecutionHolder;
import com.astamuse.asta4d.snippet.SnippetInvokeException;

public class ContextDataAutowireInterceptor implements SnippetInterceptor {

    @Override
    public boolean beforeProcess(SnippetExecutionHolder execution) throws Exception {

        try {
            Object[] params = InjectUtil.getMethodInjectParams(execution.getMethod());
            execution.setParams(params);
            return true;
        } catch (DataOperationException e) {
            throw new SnippetInvokeException(execution.getDeclarationInfo(), e);
        }

    }

    @Override
    public void afterProcess(SnippetExecutionHolder execution, ExceptionHandler exceptionHandler) {

    }

}
