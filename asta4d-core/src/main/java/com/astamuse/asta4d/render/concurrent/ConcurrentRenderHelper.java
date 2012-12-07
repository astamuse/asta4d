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

import com.astamuse.asta4d.Context;
import com.astamuse.asta4d.render.Renderer;

public class ConcurrentRenderHelper {

    private final static String INSTANCE_KEY = ConcurrentRenderHelper.class.getName() + "##instance-key";

    private CompletionService<FutureRendererHolder> cs;

    private int executionCount = 0;

    private ConcurrentRenderHelper(ExecutorService es) {
        cs = new ExecutorCompletionService<>(es);
    }

    public final static ConcurrentRenderHelper getInstance(Context context) {
        ConcurrentRenderHelper instance = context.getData(INSTANCE_KEY);
        if (instance == null) {
            instance = new ConcurrentRenderHelper(context.getConfiguration().getMultiThreadExecutor());
            context.setData(INSTANCE_KEY, instance);
        }
        return instance;
    }

    public void submitWithContext(final Context context, final String snippetRef, final Callable<Renderer> caller) {
        cs.submit(new Callable<FutureRendererHolder>() {
            @Override
            public FutureRendererHolder call() throws Exception {
                Renderer renderer = Context.with(context, caller);
                return new FutureRendererHolder(snippetRef, renderer);
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
