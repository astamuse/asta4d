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

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import com.astamuse.asta4d.Context;

public abstract class ParallelRowConvertor<S, T> implements RowConvertor<S, T> {

    public Future<T> invoke(ExecutorService es, final int rowIndex, final S data) {
        final Context context = Context.getCurrentThreadContext().clone();
        return es.submit(new Callable<T>() {
            @Override
            public T call() throws Exception {
                return Context.with(context, new Callable<T>() {
                    @Override
                    public T call() throws Exception {
                        return convert(rowIndex, data);
                    }
                });
            }
        });
    }

}
