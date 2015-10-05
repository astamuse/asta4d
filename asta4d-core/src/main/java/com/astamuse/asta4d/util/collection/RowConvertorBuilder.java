/*
 * Copyright 2015 astamuse company,Ltd.
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

import java.util.function.Function;

public interface RowConvertorBuilder {

    public static <S, T> RowConvertor<S, T> map(Function<S, T> mapper) {
        return new RowConvertor<S, T>() {
            @Override
            public T convert(int rowIndex, S obj) {
                return mapper.apply(obj);
            }
        };
    }

    public static <S, T> RowConvertor<S, T> parallel(AlternativeRowConvertor<S, T> convertor) {
        return parallel(convertor.toRowConvertor());
    }

    public static <S, T> RowConvertor<S, T> parallel(RowConvertor<S, T> convertor) {
        return new RowConvertor<S, T>() {
            @Override
            public T convert(int rowIndex, S obj) {
                return convertor.convert(rowIndex, obj);
            }

            public boolean isParallel() {
                return true;
            }
        };
    }

    public static <S, T> RowConvertor<S, T> parallel(Function<S, T> mapper) {
        return new RowConvertor<S, T>() {
            @Override
            public T convert(int rowIndex, S obj) {
                return mapper.apply(obj);
            }

            public boolean isParallel() {
                return true;
            }
        };
    }
}
