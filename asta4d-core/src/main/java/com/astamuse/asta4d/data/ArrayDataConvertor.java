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
