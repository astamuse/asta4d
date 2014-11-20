package com.astamuse.asta4d.util;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class ClassUtil {
    @SuppressWarnings("rawtypes")
    public static final List<Field> retrieveAllFieldsIncludeAllSuperClasses(Class cls) {
        Class c = cls;
        List<Field> resultList = new LinkedList<Field>();
        while (!c.getName().equals("java.lang.Object")) {
            resultList.addAll(Arrays.asList(c.getDeclaredFields()));
            c = c.getSuperclass();
        }
        return resultList;
    }

}
