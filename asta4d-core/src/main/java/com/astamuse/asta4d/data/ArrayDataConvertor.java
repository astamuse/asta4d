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

package com.astamuse.asta4d.data;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public abstract class ArrayDataConvertor<S, T> implements DataConvertor<S, T> {

    public T convertFromArray(S[] array) {
        if (array == null || array.length == 0) {
            return convert(null);
        }
        return convert(array[0]);
    }

    public T[] convertToArray(S obj) {
        if (obj == null) {
            return null;
        }
        T element = convert(obj);
        @SuppressWarnings("unchecked")
        T[] array = (T[]) Array.newInstance(element.getClass(), 1);
        Array.set(array, 0, convert(obj));
        return array;
    }

    @SuppressWarnings("unchecked")
    public T[] convertFromToArray(S[] array) {
        if (array == null || array.length == 0) {
            return null;
        }
        List<T> list = new ArrayList<>();
        for (S obj : array) {
            list.add(convert(obj));
        }
        if (list.isEmpty()) {
            return null;
        }
        return list.toArray((T[]) Array.newInstance(list.get(0).getClass(), list.size()));
    }
}
