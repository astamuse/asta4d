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

package com.astamuse.asta4d.render.concurrent;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;

import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.astamuse.asta4d.Configuration;
import com.astamuse.asta4d.Context;
import com.astamuse.asta4d.extnode.ExtNodeConstants;
import com.astamuse.asta4d.render.Renderer;

public class ConcurrentRenderHelper {

    private final static Logger logger = LoggerFactory.getLogger(ConcurrentRenderHelper.class);

    private final static String INSTANCE_KEY = ConcurrentRenderHelper.class.getName() + "##instance-key##";

    private CompletionService<FutureRendererHolder> cs;

    private int executionCount = 0;

    private ConcurrentRenderHelper(ExecutorService es) {
        cs = new ExecutorCompletionService<>(es);
    }

    public final static ConcurrentRenderHelper getInstance(Context context, Document doc) {
        String docRef = doc.attr(ExtNodeConstants.ATTR_DOC_REF);
        String key = INSTANCE_KEY + docRef;
        ConcurrentRenderHelper instance = context.getData(key);
        if (instance == null) {
            instance = new ConcurrentRenderHelper(Configuration.getConfiguration().getSnippetExecutorFactory().getExecutorService());
            context.setData(key, instance);
        }
        return instance;
    }

    public void submitWithContext(final Context context, final String snippetRef, final Callable<Renderer> caller) {
        cs.submit(new Callable<FutureRendererHolder>() {
            @Override
            public FutureRendererHolder call() throws Exception {
                /*
                 * An exception from paralleled snippet may be ignored when there is another exception 
                 * being thrown on head.
                 * 
                 * In multiple threads, the original exception may cause some other exceptions in other threads, 
                 * which may be identified before the original exception so that the following #take()
                 * method will never be called thus the original exception will never be identified.
                 * 
                 * Commonly, it is not a problem, but it is difficult to debug since the original exception had been
                 * ignored and there is no any information about it at anywhere. So at least, we output the exception
                 * in log to help debug.
                 */
                try {
                    Renderer renderer = Context.with(context, caller);
                    return new FutureRendererHolder(snippetRef, renderer);
                } catch (Exception ex) {
                    logger.error("", ex);
                    throw ex;
                }
            }
        });
        executionCount++;
    }

    public boolean hasUnCompletedTask() {
        return executionCount > 0;
    }

    public FutureRendererHolder take() throws InterruptedException, ExecutionException {
        FutureRendererHolder holder = cs.take().get();
        executionCount--;
        return holder;

    }
}
