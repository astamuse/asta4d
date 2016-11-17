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
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.astamuse.asta4d.Configuration;
import com.astamuse.asta4d.Context;

public class ListConvertUtil {

    // private final static Logger logger = LoggerFactory.getLogger(ListConvertUtil.class);

    private final static String ParallelListConversionMark = "ParallelListConversionMark##" + ListConvertUtil.class.getName();

    private final static ExecutorService ListExecutorService;

    private final static ExecutorService ParallelFallbackExecutor;

    static {
        Configuration conf = Configuration.getConfiguration();
        ListExecutorService = conf.getParallelListConvertingExecutorFactory().createExecutorService();
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
                    return invokeByExecutor(ListExecutorService, sourceList, convertor, conf.getNumberLimitOfParallelListConverting());
                });
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

        } // end else in non recursive
    }

    private static <S, T> List<Future<T>> invokeByExecutor(ExecutorService taskService, Iterable<S> sourceList,
            RowConvertor<S, T> convertor, int maxParallelNumber) {

        /*
         * At first, we extract and dispatch the source list elements to slots as following order:
         * slot 0, slot 1, slot 2, ..., slot n
         * 0,      1,      2,      ..., n-1
         * n,      n+1,    n+2,    ..., 2n-1
         * .
         * .
         * .
         * xn,     xn+1,   xn+2,  xn+3(last)
         * 
         * Then we dispatch each slot to executor service to perform the transforming, after that, we build a future list 
         * which contains delegated future which delegate all the future methods to the future of corresponding slot and 
         * then retrieve the corresponding element by calculated index.
         */
        @SuppressWarnings("unchecked")
        List<S>[] groupedListArray = new List[maxParallelNumber];
        for (int i = 0; i < maxParallelNumber; i++) {
            groupedListArray[i] = new LinkedList<>();
        }
        Iterator<S> srcIt = sourceList.iterator();
        int count = 0;
        while (srcIt.hasNext()) {
            groupedListArray[count % maxParallelNumber].add(srcIt.next());
            count++;
        }
        @SuppressWarnings("unchecked")
        Future<T>[] futureArray = new Future[count];
        final Context context = Context.getCurrentThreadContext();
        for (int i = 0; i < maxParallelNumber; i++) {
            List<S> groupedList = groupedListArray[i];
            if (groupedList.isEmpty()) {
                continue;
            }
            final Future<List<T>> f = taskService.submit(() -> {
                return Context.with(context, () -> {
                    // NOTE: this newList must be ArrayList for later retrieving performance
                    List<T> newList = new ArrayList<>(groupedList.size());
                    Iterator<S> it = groupedList.iterator();
                    int idx = 0;
                    while (it.hasNext()) {
                        newList.add(convertor.convert(idx, it.next()));
                        idx++;
                    }
                    return newList;
                });
            });
            for (int k = 0; k < groupedList.size(); k++) {
                final int fk = k;
                futureArray[k * maxParallelNumber + i] = new Future<T>() {

                    @Override
                    public boolean cancel(boolean mayInterruptIfRunning) {
                        return f.cancel(mayInterruptIfRunning);
                    }

                    @Override
                    public boolean isCancelled() {
                        return f.isCancelled();
                    }

                    @Override
                    public boolean isDone() {
                        return f.isDone();
                    }

                    @Override
                    public T get() throws InterruptedException, ExecutionException {
                        List<T> list = f.get();
                        // the list is promised to be ArrayList so that there is no performance issue on get invoking
                        return list.get(fk);
                    }

                    @Override
                    public T get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
                        List<T> list = f.get(timeout, unit);
                        // the list is promised to be ArrayList so that there is no performance issue on get invoking
                        return list.get(fk);
                    }
                };
            }
        }
        return Arrays.asList(futureArray);

    }
}
