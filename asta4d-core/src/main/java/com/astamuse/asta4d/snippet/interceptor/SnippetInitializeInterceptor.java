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

import java.util.ArrayList;
import java.util.List;

import com.astamuse.asta4d.Context;
import com.astamuse.asta4d.interceptor.base.ExceptionHandler;
import com.astamuse.asta4d.snippet.InitializableSnippet;
import com.astamuse.asta4d.snippet.SnippetExecutionHolder;

public class SnippetInitializeInterceptor implements SnippetInterceptor {

    private final static String InstanceListCacheKey = SnippetInitializeInterceptor.class + "##InstanceListCacheKey##";

    @Override
    public boolean beforeProcess(SnippetExecutionHolder execution) throws Exception {

        Context context = Context.getCurrentThreadContext();
        List<Object> snippetList = context.getData(InstanceListCacheKey);
        if (snippetList == null) {
            snippetList = new ArrayList<>();
            context.setData(InstanceListCacheKey, snippetList);
        }

        Object target = execution.getInstance();
        boolean inialized = false;

        // TODO we need a more efficient way to avoid global lock
        synchronized (snippetList) {
            for (Object initializedSnippet : snippetList) {
                if (initializedSnippet == target) {
                    inialized = true;
                    break;
                }
            }
        }

        if (!inialized) {
            if (target instanceof InitializableSnippet) {
                ((InitializableSnippet) target).init();
            }
            synchronized (snippetList) {
                snippetList.add(target);
            }
        }

        return true;

    }

    @Override
    public void afterProcess(SnippetExecutionHolder execution, ExceptionHandler exceptionHandler) {

    }

}
