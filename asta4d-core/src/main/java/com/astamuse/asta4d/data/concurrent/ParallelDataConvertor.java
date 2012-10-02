package com.astamuse.asta4d.data.concurrent;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import com.astamuse.asta4d.data.DataConvertor;

public abstract class ParallelDataConvertor<S, T> implements DataConvertor<S, T> {

    public Future<T> invoke(ExecutorService es, final S data) {
        return es.submit(new Callable<T>() {
            @Override
            public T call() throws Exception {
                return convert(data);
            }
        });
    }
}
