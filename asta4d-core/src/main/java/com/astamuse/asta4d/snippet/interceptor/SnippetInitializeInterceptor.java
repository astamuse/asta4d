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
import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.astamuse.asta4d.Context;
import com.astamuse.asta4d.data.InjectUtil;
import com.astamuse.asta4d.interceptor.base.ExceptionHandler;
import com.astamuse.asta4d.snippet.InitializableSnippet;
import com.astamuse.asta4d.snippet.SnippetExecutionHolder;

public class SnippetInitializeInterceptor implements SnippetInterceptor {

    private static class InitializedListHolder {
        ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
        List<Object> snippetList = new ArrayList<>();
    }

    private final static String InstanceListCacheKey = SnippetInitializeInterceptor.class + "##InstanceListCacheKey##";

    public static final void initContext(Context context) {
        context.setData(InstanceListCacheKey, new InitializedListHolder());
    }

    @Override
    public boolean beforeProcess(SnippetExecutionHolder execution) throws Exception {

        Context context = Context.getCurrentThreadContext();

        // the list would not be null
        InitializedListHolder listHolder = context.getData(InstanceListCacheKey);

        Object target = execution.getInstance();
        boolean inialized = false;

        listHolder.lock.readLock().lock();
        try {
            for (Object initializedSnippet : listHolder.snippetList) {
                if (initializedSnippet == target) {
                    inialized = true;
                    break;
                }
            }
        } finally {
            listHolder.lock.readLock().unlock();
        }
        if (!inialized) {
            // retrieve write lock
            listHolder.lock.writeLock().lock();
            try {
                // check again
                for (Object initializedSnippet : listHolder.snippetList) {
                    if (initializedSnippet == target) {
                        inialized = true;
                        break;
                    }
                }
                // do the initialization
                if (!inialized) {
                    InjectUtil.injectToInstance(target);
                    if (target instanceof InitializableSnippet) {
                        ((InitializableSnippet) target).init();
                    }

                    listHolder.snippetList.add(target);
                }
            } finally {
                listHolder.lock.writeLock().unlock();
            }
        }

        return true;

    }

    @Override
    public void afterProcess(SnippetExecutionHolder execution, ExceptionHandler exceptionHandler) {

    }

}
