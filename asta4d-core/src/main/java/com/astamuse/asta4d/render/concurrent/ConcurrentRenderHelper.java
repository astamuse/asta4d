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
