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

package com.astamuse.asta4d.util.concurrent;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

import com.astamuse.asta4d.Configuration;

public class DefaultExecutorServiceFactory implements ExecutorServiceFactory {

    private final static AtomicInteger instanceCounter = new AtomicInteger();

    private AtomicInteger counter = new AtomicInteger();

    private String threadName;

    private int poolSize;

    public DefaultExecutorServiceFactory() {
        this(200);
    }

    public DefaultExecutorServiceFactory(int poolSize) {
        this("asta4d", poolSize);
    }

    public DefaultExecutorServiceFactory(String threadName, int poolSize) {
        this.threadName = threadName;
        this.poolSize = poolSize;
    }

    public String getThreadName() {
        return threadName;
    }

    public void setThreadName(String threadName) {
        this.threadName = threadName;
    }

    public int getPoolSize() {
        return poolSize;
    }

    public void setPoolSize(int poolSize) {
        this.poolSize = poolSize;
    }

    @Override
    public ExecutorService createExecutorService() {
        final int instanceId = instanceCounter.incrementAndGet();
        ExecutorService es = Executors.newFixedThreadPool(poolSize, new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                String name = threadName + "-" + instanceId + "-t-" + counter.incrementAndGet();
                return new Thread(r, name);
            }
        });
        Configuration.getConfiguration().addShutdownHookers(() -> {
            es.shutdown();
        });
        return es;
    }

}
