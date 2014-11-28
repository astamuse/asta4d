package com.astamuse.asta4d.util;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;

import com.thoughtworks.paranamer.Paranamer;

public class Java8Paranamer implements Paranamer {

    @Override
    public String[] lookupParameterNames(AccessibleObject methodOrConstructor) {
        return lookupParameterNames(methodOrConstructor, true);
    }

    @Override
    public String[] lookupParameterNames(AccessibleObject methodOrConstructor, boolean throwExceptionIfMissing) {
        Parameter[] parameters = getParameters(methodOrConstructor);
        return Arrays.stream(parameters).map(p -> {
            return p.getName();
        }).toArray(String[]::new);
    }

    private Parameter[] getParameters(AccessibleObject methodOrConstructor) {
        if (methodOrConstructor instanceof Method) {
            Method method = (Method) methodOrConstructor;
            return method.getParameters();
        } else {
            Constructor<?> constructor = (Constructor<?>) methodOrConstructor;
            return constructor.getParameters();
        }
    }

}
