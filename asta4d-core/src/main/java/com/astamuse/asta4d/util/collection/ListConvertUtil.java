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
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import com.astamuse.asta4d.Context;
import com.astamuse.asta4d.data.DataConvertor;
import com.astamuse.asta4d.data.concurrent.ParallelDataConvertor;

public class ListConvertUtil {

    public final static <S, T> List<T> transform(Iterable<S> sourceList, RowConvertor<S, T> convertor) {
        List<T> newList = new LinkedList<>();
        Iterator<S> it = sourceList.iterator();
        int idx = 0;
        while (it.hasNext()) {
            newList.add(convertor.convert(idx, it.next()));
        }
        return new ArrayList<>(newList);
    }

    public final static <S, T> List<T> transform(Iterable<S> sourceList, ParallelRowConvertor<S, T> convertor) {
        ExecutorService executor = Context.getCurrentThreadContext().getConfiguration().getMultiThreadExecutor();
        List<Future<T>> futureList = new LinkedList<>();
        int index = 0;
        for (S obj : sourceList) {
            futureList.add(convertor.invoke(executor, index, obj));
            index++;
        }
        List<T> newList = new ArrayList<>(index);
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

    public final static <S, T> List<Future<T>> transformToFuture(Iterable<S> sourceList, ParallelRowConvertor<S, T> convertor) {
        ExecutorService executor = Context.getCurrentThreadContext().getConfiguration().getMultiThreadExecutor();
        List<Future<T>> futureList = new LinkedList<>();
        int index = 0;
        for (S obj : sourceList) {
            futureList.add(convertor.invoke(executor, index, obj));
            index++;
        }
        return futureList;
    }

    @Deprecated
    public final static <S, T> List<T> transform(Iterable<S> sourceList, final DataConvertor<S, T> convertor) {
        RowConvertor<S, T> rc = new RowConvertor<S, T>() {
            @Override
            public T convert(int rowIndex, S obj) {
                return convertor.convert(obj);
            }
        };
        return transform(sourceList, rc);
    }

    @Deprecated
    public final static <S, T> List<T> transform(Iterable<S> sourceList, final ParallelDataConvertor<S, T> convertor) {
        ParallelRowConvertor<S, T> rc = new ParallelRowConvertor<S, T>() {
            @Override
            public T convert(int rowIndex, S obj) {
                return convertor.convert(obj);
            }
        };
        return transform(sourceList, rc);
    }

    @Deprecated
    public final static <S, T> List<Future<T>> transformToFuture(Iterable<S> sourceList, final ParallelDataConvertor<S, T> convertor) {
        ParallelRowConvertor<S, T> rc = new ParallelRowConvertor<S, T>() {
            @Override
            public T convert(int rowIndex, S obj) {
                return convertor.convert(obj);
            }
        };
        return transformToFuture(sourceList, rc);
    }

}
