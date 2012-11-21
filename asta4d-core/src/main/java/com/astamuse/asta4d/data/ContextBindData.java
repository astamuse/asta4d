package com.astamuse.asta4d.data;

import java.util.concurrent.atomic.AtomicInteger;

import com.astamuse.asta4d.Context;

public class ContextBindData<R> {

    public static interface DataProvider<R> {
        public R get();
    }

    private final static AtomicInteger sequencer = new AtomicInteger();

    private String bindKey = "bindKey-" + sequencer.incrementAndGet() + "##" + ContextBindData.class.getName();

    // private T THIS = null;

    private DataProvider<R> provider;

    private ContextBindData() {
        // TODO Auto-generated constructor stub
    }

    public R get() {
        Context context = Context.getCurrentThreadContext();
        R r = context.getData(bindKey);
        if (r == null) {
            r = provider.get();
            context.setData(bindKey, r);
        }
        return r;
    }

    public final static <R> ContextBindData<R> create(DataProvider<R> provider) {
        ContextBindData<R> data = new ContextBindData<>();
        // data.THIS = THIS;
        data.provider = provider;
        return data;
    }
}
