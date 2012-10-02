package com.astamuse.asta4d.concurrent;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ExecutorServiceFactory {

    public final static ExecutorService getCachableThreadExecutor() {
        // TODO is the SynchronousQueue appropriate?
        return new ThreadPoolExecutor(250, Integer.MAX_VALUE, 60L, TimeUnit.SECONDS, new SynchronousQueue<Runnable>());
    }

}
