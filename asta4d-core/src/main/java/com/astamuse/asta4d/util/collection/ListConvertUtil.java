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
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.astamuse.asta4d.Configuration;
import com.astamuse.asta4d.Context;
import com.astamuse.asta4d.util.concurrent.ListExecutorServiceUtil;

public class ListConvertUtil {

    private final static String ParallelListConversionMark = "ParallelListConversionMark##" + ListConvertUtil.class.getName();

    private final static ExecutorService ParallelFallbackExecutor = Executors.newCachedThreadPool();

    public final static <S, T> List<T> transform(Iterable<S> sourceList, RowConvertor<S, T> convertor) {
        List<T> newList = new LinkedList<>();
        Iterator<S> it = sourceList.iterator();
        int idx = 0;
        while (it.hasNext()) {
            newList.add(convertor.convert(idx, it.next()));
            idx++;
        }
        return new ArrayList<>(newList);
    }

    public final static <S, T> List<T> transform(Iterable<S> sourceList, ParallelRowConvertor<S, T> convertor) {
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
    }

    public final static <S, T> List<Future<T>> transformToFuture(final Iterable<S> sourceList, final ParallelRowConvertor<S, T> convertor) {
        final Context context = Context.getCurrentThreadContext();
        final Configuration conf = Configuration.getConfiguration();
        Boolean isInParallelConverting = context.getData(ParallelListConversionMark);

        if (isInParallelConverting != null) {// recursive converting
            switch (conf.getParallelRecursivePolicyForListRendering()) {
            case EXCEPTION:
                throw new RuntimeException(
                        "Parallel list converting is forbidden (by default) to avoid deadlock. You can change this policy by Configuration.setParallelRecursivePolicyForListRendering().");
            case CURRENT_THREAD:
                List<T> list = transform(sourceList, (RowConvertor<S, T>) convertor);
                return transform(list, new RowConvertor<T, Future<T>>() {
                    @Override
                    public Future<T> convert(int rowIndex, final T obj) {
                        return new Future<T>() {
                            @Override
                            public boolean cancel(boolean mayInterruptIfRunning) {
                                return false;
                            }

                            @Override
                            public boolean isCancelled() {
                                return false;
                            }

                            @Override
                            public boolean isDone() {
                                return true;
                            }

                            @Override
                            public T get() throws InterruptedException, ExecutionException {
                                return obj;
                            }

                            @Override
                            public T get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
                                return obj;
                            }
                        };
                    }
                });
            case NEW_THREAD:
                ExecutorService executor = ParallelFallbackExecutor;
                List<Future<T>> futureList = new LinkedList<>();
                int index = 0;
                for (S obj : sourceList) {
                    futureList.add(convertor.invoke(executor, index, obj));
                    index++;
                }
                return futureList;
            default:
                return Collections.emptyList();
            }
        } else {// not in recursive converting
            Context newContext = context.clone();
            newContext.setData(ParallelListConversionMark, Boolean.TRUE);
            try {
                return Context.with(newContext, new Callable<List<Future<T>>>() {
                    @Override
                    public List<Future<T>> call() throws Exception {
                        ExecutorService executor = ListExecutorServiceUtil.getExecutorService();
                        List<Future<T>> futureList = new LinkedList<>();
                        int index = 0;
                        for (S obj : sourceList) {
                            futureList.add(convertor.invoke(executor, index, obj));
                            index++;
                        }
                        return futureList;
                    }
                });
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

        }

    }

}
