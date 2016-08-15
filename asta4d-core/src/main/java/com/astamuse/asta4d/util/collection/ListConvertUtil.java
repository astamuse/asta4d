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

package com.astamuse.asta4d.util.collection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.astamuse.asta4d.Configuration;
import com.astamuse.asta4d.Context;
import com.astamuse.asta4d.util.SyncClosureReference;

public class ListConvertUtil {

    private final static Logger logger = LoggerFactory.getLogger(ListConvertUtil.class);

    private final static String ParallelListConversionMark = "ParallelListConversionMark##" + ListConvertUtil.class.getName();

    private final static ExecutorService ListExecutorService;

    private final static ExecutorService ListDispatchExecutorService;

    private final static ExecutorService ParallelFallbackExecutor;

    static {
        Configuration conf = Configuration.getConfiguration();
        ListExecutorService = conf.getParallelListConvertingExecutorFactory().createExecutorService();
        ListDispatchExecutorService = conf.getParallelListConvertingDispatchExecutorFactory().createExecutorService();
        ParallelFallbackExecutor = Executors.newCachedThreadPool();
    }

    private final static <S, T> List<T> _transform(Iterable<S> sourceList, RowConvertor<S, T> convertor) {
        List<T> newList = new LinkedList<>();
        Iterator<S> it = sourceList.iterator();
        int idx = 0;
        while (it.hasNext()) {
            newList.add(convertor.convert(idx, it.next()));
            idx++;
        }
        return new ArrayList<>(newList);
    }

    public final static <S, T> List<T> transform(Iterable<S> sourceList, RowConvertor<S, T> convertor) {
        if (convertor.isParallel()) {
            List<Future<T>> futureList = transformToFuture(sourceList, convertor);
            List<T> newList = new ArrayList<>(futureList.size());
            Iterator<Future<T>> it = futureList.iterator();
            try {
                while (it.hasNext()) {
                    newList.add(it.next().get());
                }
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
            return newList;
        } else {
            return _transform(sourceList, convertor);
        }
    }

    public final static <S, T> List<Future<T>> transformToFuture(final Iterable<S> sourceList, final RowConvertor<S, T> convertor) {
        final Context context = Context.getCurrentThreadContext();
        final Configuration conf = Configuration.getConfiguration();
        Boolean isInParallelConverting = context.getData(ParallelListConversionMark);

        // for non-parallel converting, we will force to current thread converting.
        boolean doParallel = convertor.isParallel();
        ParallelRecursivePolicy policy = doParallel ? conf.getRecursivePolicyForParallelConverting()
                : ParallelRecursivePolicy.CURRENT_THREAD;

        if (isInParallelConverting != null || !doParallel) {// recursive converting or non-parallel
            switch (policy) {
            case EXCEPTION:
                throw new RuntimeException(
                        "Recursive parallel list converting is forbidden (by default) to avoid deadlock. You can change this policy by Configuration.setRecursivePolicyForParallelListConverting().");
            case CURRENT_THREAD:
                List<T> list = _transform(sourceList, convertor);
                return transform(list, new RowConvertor<T, Future<T>>() {
                    @Override
                    public Future<T> convert(int rowIndex, final T obj) {
                        return new Future<T>() {
                            @Override
                            public final boolean cancel(boolean mayInterruptIfRunning) {
                                return false;
                            }

                            @Override
                            public final boolean isCancelled() {
                                return false;
                            }

                            @Override
                            public final boolean isDone() {
                                return true;
                            }

                            @Override
                            public final T get() throws InterruptedException, ExecutionException {
                                return obj;
                            }

                            @Override
                            public final T get(long timeout, TimeUnit unit)
                                    throws InterruptedException, ExecutionException, TimeoutException {
                                return obj;
                            }
                        };
                    }
                });
            case NEW_THREAD:
                return invokeByExecutor(ParallelFallbackExecutor, sourceList, convertor, 2);
            default:
                return Collections.emptyList();
            }
        } else {// in non recursive converting
            Context newContext = context.clone();
            newContext.setData(ParallelListConversionMark, Boolean.TRUE);
            try {
                return Context.with(newContext, () -> {
                    return dispatchToExecutor(ListDispatchExecutorService, ListExecutorService, sourceList, convertor,
                            conf.getNumberLimitOfParallelListConverting());
                });
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

        } // end else in non recursive
    }

    private static <S, T> List<Future<T>> dispatchToExecutor(ExecutorService taskDispatchService, ExecutorService taskService,
            Iterable<S> sourceList, RowConvertor<S, T> convertor, int maxParallelNumber) {
        Collection<S> sourceCollection;
        if (sourceList instanceof Collection) {
            sourceCollection = (Collection<S>) sourceList;
        } else {
            sourceCollection = new LinkedList<>();
            for (S obj : sourceList) {
                sourceCollection.add(obj);
            }
        }

        int size = sourceCollection.size();

        SyncClosureReference<Future<T>[]> futureArrayRef = new SyncClosureReference<>();
        SyncClosureReference<Exception> exRef = new SyncClosureReference<>();
        final Context context = Context.getCurrentThreadContext();
        Future<List<Future<T>>> dispatchResult = taskDispatchService.submit(() -> {
            return Context.with(context, () -> {
                return invokeByExecutor(taskService, sourceCollection, convertor, maxParallelNumber);
            });
        });

        List<Future<T>> delegatedList = new LinkedList<>();
        for (int i = 0; i < size; i++) {
            int idx = i;
            delegatedList.add(new Future<T>() {
                private void waitForDispatched() {
                    if (exRef.ref != null || futureArrayRef.ref != null) {
                        return;
                    }
                    // wait for dispatch future
                    try {
                        List<Future<T>> futureList = dispatchResult.get();
                        @SuppressWarnings("unchecked")
                        Future<T>[] fa = new Future[futureList.size()];
                        futureArrayRef.ref = futureList.toArray(fa);
                    } catch (InterruptedException | ExecutionException e) {
                        exRef.ref = e;
                    }
                }

                @Override
                public boolean cancel(boolean mayInterruptIfRunning) {
                    if (dispatchResult.cancel(mayInterruptIfRunning)) {
                        return true;
                    } else {
                        waitForDispatched();
                        if (exRef.ref != null) {
                            return false;
                        } else {
                            return futureArrayRef.ref[idx].cancel(mayInterruptIfRunning);
                        }
                    }
                }

                @Override
                public boolean isCancelled() {
                    if (dispatchResult.isCancelled()) {
                        return true;
                    } else {
                        waitForDispatched();
                        if (exRef.ref != null) {
                            return false;// it is not so sure
                        } else {
                            return futureArrayRef.ref[idx].isCancelled();
                        }
                    }
                }

                @Override
                public boolean isDone() {
                    if (dispatchResult.isDone()) {
                        waitForDispatched();
                        if (exRef.ref != null) {
                            return false;// it is not so sure
                        } else {
                            return futureArrayRef.ref[idx].isDone();
                        }
                    } else {
                        return false;
                    }
                }

                @Override
                public T get() throws InterruptedException, ExecutionException {
                    waitForDispatched();
                    if (exRef.ref != null) {
                        throw new ExecutionException(exRef.ref);
                    } else {
                        return futureArrayRef.ref[idx].get();
                    }
                }

                @Override
                public T get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
                    waitForDispatched();
                    if (exRef.ref != null) {
                        throw new ExecutionException(exRef.ref);
                    } else {
                        return futureArrayRef.ref[idx].get(timeout, unit);
                    }
                }
            });
        } // end for add to list
        return delegatedList;
    }

    private static <S, T> List<Future<T>> invokeByExecutor(ExecutorService taskService, Iterable<S> sourceList,
            RowConvertor<S, T> convertor, int maxParallelNumber) {
        try {
            final Semaphore available = new Semaphore(maxParallelNumber, false);
            final Context context = Context.getCurrentThreadContext();
            List<Future<T>> futureList = new LinkedList<>();
            int index = 0;
            Future<T> f;
            for (S obj : sourceList) {
                final int rowIndex = index;
                // logger.debug("acquiring:{}", rowIndex);
                available.acquire();
                // logger.debug("acquired:{}", rowIndex);
                f = taskService.submit(new Callable<T>() {
                    @Override
                    public T call() throws Exception {
                        try {
                            return Context.with(context, new Callable<T>() {
                                @Override
                                public T call() throws Exception {
                                    return convertor.convert(rowIndex, obj);
                                }
                            });
                        } finally {
                            // logger.debug("released:{}", rowIndex);
                            available.release();
                        }
                    }
                });
                futureList.add(f);
                index++;
            }
            return futureList;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}
