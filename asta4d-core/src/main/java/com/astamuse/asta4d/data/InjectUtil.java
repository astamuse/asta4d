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

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.astamuse.asta4d.Configuration;
import com.astamuse.asta4d.Context;
import com.astamuse.asta4d.data.annotation.ContextData;
import com.astamuse.asta4d.data.annotation.ContextDataSet;
import com.astamuse.asta4d.util.Asta4DWarningException;
import com.thoughtworks.paranamer.AdaptiveParanamer;
import com.thoughtworks.paranamer.Paranamer;

/**
 * This class is a function holder to supply functionalities about data
 * injection
 * 
 * @author e-ryu
 * 
 */
public class InjectUtil {

    private final static Logger logger = LoggerFactory.getLogger(InjectUtil.class);

    /**
     * A class that present the injectable target information
     * 
     * @author e-ryu
     * 
     */
    private static class TargetInfo {
        String name;
        String scope;
        Class<?> type;
        Object defaultValue;
        boolean isContextDataSet;

        void fixForPrimitiveType() {
            TypeInfo typeInfo = new TypeInfo(type);
            type = typeInfo.getType();
            defaultValue = typeInfo.getDefaultValue();
        }
    }

    private static class MethodInfo extends TargetInfo {
        Method method;
    }

    private static class FieldInfo extends TargetInfo {
        Field field;
    }

    private static class InstanceWireTarget {
        List<FieldInfo> setFieldList = new ArrayList<>();
        List<FieldInfo> getFieldList = new ArrayList<>();
        List<MethodInfo> setMethodList = new ArrayList<>();
        List<MethodInfo> getMethodList = new ArrayList<>();
    }

    private final static ConcurrentHashMap<String, InstanceWireTarget> InstanceTargetCache = new ConcurrentHashMap<>();

    private final static ConcurrentHashMap<Method, List<TargetInfo>> MethodTargetCache = new ConcurrentHashMap<>();

    private final static Paranamer paranamer = new AdaptiveParanamer();

    /**
     * Set the value of all the fields marked by {@link ContextData} of the
     * given instance.
     * 
     * @param instance
     * @throws DataOperationException
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public final static void injectToInstance(Object instance) throws DataOperationException {
        try {
            Context context = Context.getCurrentThreadContext();
            InstanceWireTarget target = getInstanceTarget(instance);
            ContextDataFinder dataFinder = Configuration.getConfiguration().getContextDataFinder();
            ContextDataHolder searchHolder, valueHolder;
            Object value;
            for (FieldInfo fi : target.setFieldList) {
                if (fi.isContextDataSet) {
                    value = fi.type.newInstance();
                    injectToInstance(value);
                    FieldUtils.writeField(fi.field, instance, value, true);
                } else if (ContextDataHolder.class.isAssignableFrom(fi.type)) {
                    Object hi = FieldUtils.readField(fi.field, instance);
                    if (hi == null) {
                        hi = fi.type.newInstance();
                        FieldUtils.writeField(fi.field, instance, hi, true);
                    }
                    valueHolder = (ContextDataHolder) hi;
                    Class searchType = valueHolder.getTypeCls();
                    if (searchType == null) {
                        throw new DataOperationException(
                                fi.field.getName() +
                                        " should be initialized at first or we can not retrieve the type you want since it is a type of CotnextDataHolder. " +
                                        "You can also define an extended class to return the type class, in this case, you do not need to initialized it by your self");
                    }
                    searchHolder = dataFinder.findDataInContext(context, fi.scope, fi.name, searchType);
                    if (searchHolder != null) {// found data!!!
                        valueHolder.setValue(searchHolder.getScope(), searchHolder.getName(), searchHolder.getValue());
                    } else {
                        valueHolder.setValue("#DefaultValue", fi.name, fi.defaultValue);
                    }
                } else {
                    searchHolder = dataFinder.findDataInContext(context, fi.scope, fi.name, fi.type);
                    if (searchHolder != null) {
                        FieldUtils.writeField(fi.field, instance, searchHolder.getValue(), true);
                    } else {
                        FieldUtils.writeField(fi.field, instance, fi.defaultValue, true);
                    }
                }
            }

            for (MethodInfo mi : target.setMethodList) {
                if (mi.isContextDataSet) {
                    value = mi.type.newInstance();
                    injectToInstance(value);
                } else if (ContextDataHolder.class.isAssignableFrom(mi.type)) {
                    Object hi = mi.type.newInstance();
                    valueHolder = (ContextDataHolder) hi;
                    Class searchType = valueHolder.getTypeCls();
                    if (searchType == null) {
                        throw new DataOperationException(mi.method.getName() + " cannot initialize an instance of " +
                                valueHolder.getClass().getName() + ". You should define an extended class to return the type class");
                    }
                    searchHolder = dataFinder.findDataInContext(context, mi.scope, mi.name, searchType);
                    if (searchHolder != null) {// found data!!!
                        valueHolder.setValue(searchHolder.getScope(), searchHolder.getName(), searchHolder.getValue());
                    } else {
                        valueHolder.setValue("#DefaultValue", mi.name, mi.defaultValue);
                    }
                    value = valueHolder;
                } else {
                    searchHolder = dataFinder.findDataInContext(context, mi.scope, mi.name, mi.type);
                    if (searchHolder != null) {
                        value = searchHolder.getValue();
                    } else {
                        value = mi.defaultValue;
                    }
                }

                if (value == null) {
                    value = mi.defaultValue;
                }

                mi.method.invoke(instance, value);
            }
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | InstantiationException e) {
            throw new DataOperationException("Exception when inject value to instance of " + instance.getClass().toString(), e);
        }
    }

    /**
     * Retrieve values from fields marked as reverse injectable of given
     * instance.
     * 
     * There are only limited scopes can be marked as injectable. See
     * {@link Configuration#setReverseInjectableScopes(List)}.
     * 
     * @param instance
     * @throws DataOperationException
     */
    public final static void setContextDataFromInstance(Object instance) throws DataOperationException {
        try {
            Context context = Context.getCurrentThreadContext();
            InstanceWireTarget target = getInstanceTarget(instance);
            Object value;
            for (FieldInfo fi : target.getFieldList) {
                value = FieldUtils.readField(fi.field, instance, true);
                context.setData(fi.scope, fi.name, value);
            }

            for (MethodInfo mi : target.getMethodList) {
                value = mi.method.invoke(instance);
                context.setData(mi.scope, mi.name, value);
            }
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            String msg = String.format("Exception when inject value from instance of [%s] to Context.", instance.getClass().toString());
            throw new DataOperationException(msg, e);
        }
    }

    private final static InstanceWireTarget getInstanceTarget(Object instance) throws DataOperationException {
        boolean cacheEnable = Configuration.getConfiguration().isCacheEnable();
        InstanceWireTarget target = null;
        if (cacheEnable) {
            String key = instance.getClass().getName();
            target = InstanceTargetCache.get(key);
            if (target == null) {
                target = createInstanceTarget(instance);
                InstanceTargetCache.put(key, target);
            }
        } else {
            target = createInstanceTarget(instance);
        }
        return target;
    }

    private final static InstanceWireTarget createInstanceTarget(Object instance) throws DataOperationException {
        List<String> reverseTargetScopes = Configuration.getConfiguration().getReverseInjectableScopes();

        InstanceWireTarget target = new InstanceWireTarget();
        Class<?> cls = instance.getClass();

        ContextData cd;

        // at first, retrieve methods information

        // TODO we should use class name to confirm contextdata annotation
        // because they are possibly from different class loader. The problem is
        // whether it is a problem?
        Method[] mtds = cls.getMethods();
        for (Method method : mtds) {
            cd = findContextDataAnnotation(method.getAnnotations());
            if (cd != null) {
                // cd = method.getAnnotation(ContextData.class);
                MethodInfo mi = new MethodInfo();

                mi.method = method;

                boolean isGet = false;
                boolean isSet = false;
                String declaredName = cd.name();

                if (StringUtils.isEmpty(declaredName)) {
                    String name = method.getName();
                    if (name.startsWith("set")) {
                        name = name.substring(3);
                        isSet = true;
                    } else if (name.startsWith("get")) {
                        name = name.substring(3);
                        isGet = true;
                    } else if (name.startsWith("is")) {
                        name = name.substring(2);
                        isSet = true;
                    } else {

                        switch (method.getParameterTypes().length) {
                        case 0:
                            isGet = true;
                            break;
                        case 1:
                            isSet = true;
                            break;
                        default:
                            String msg = String.format("Method [%s]:[%s] can not be treated as a getter or setter method.", cls.getName(),
                                    method.toGenericString());
                            throw new DataOperationException(msg);

                        }
                    }
                    char[] cs = name.toCharArray();
                    cs[0] = Character.toLowerCase(cs[0]);
                    mi.name = new String(cs);

                } else {
                    mi.name = declaredName;
                    int typeLength = method.getParameterTypes().length;
                    if (typeLength == 0) {
                        isGet = true;
                    } else if (typeLength == 1) {
                        isSet = true;
                    } else {
                        String msg = String.format(
                                "Only one parameter is allowed on a method declared with ContextData annoataion.(%s:%s)", cls.getName(),
                                mi.name);
                        throw new DataOperationException(msg);
                    }
                }
                mi.scope = cd.scope();

                if (isGet) {
                    // only if the reverse value is explicitly set to true and
                    // the scope is contained in the allowing reverse injection
                    // list
                    if (cd.reverse()) {
                        if (reverseTargetScopes.contains(mi.scope)) {
                            mi.type = method.getReturnType();
                            mi.fixForPrimitiveType();
                            target.getMethodList.add(mi);
                        } else {
                            String msg = String.format(
                                    "Only scope in [%s] can be marked as reverse injectable but found scope as %s (%s:%s).",
                                    reverseTargetScopes.toString(), mi.scope, cls.getName(), mi.name);
                            Asta4DWarningException awe = new Asta4DWarningException(msg);
                            logger.warn(msg, awe);
                        }
                    }
                }

                if (isSet) {
                    mi.type = method.getParameterTypes()[0];
                    mi.fixForPrimitiveType();

                    ContextDataSet cdSet = findContextDataSetAnnotation(mi.type);
                    if (cdSet == null) {
                        mi.isContextDataSet = false;
                    } else {
                        mi.isContextDataSet = true;
                    }

                    target.setMethodList.add(mi);
                }

            }
        }

        // then retrieve fields information
        String objCls = Object.class.getName();
        Field[] flds;
        FieldInfo fi;
        while (!cls.getName().equals(objCls)) {
            flds = cls.getDeclaredFields();
            for (Field field : flds) {
                cd = findContextDataAnnotation(field.getAnnotations());
                if (cd != null) {
                    fi = new FieldInfo();
                    fi.field = field;
                    fi.type = field.getType();

                    String delcaredName = cd == null ? "" : cd.name();
                    if (StringUtils.isEmpty(delcaredName)) {
                        fi.name = field.getName();
                    } else {
                        fi.name = cd.name();
                    }
                    fi.scope = cd == null ? "" : cd.scope();
                    fi.fixForPrimitiveType();

                    ContextDataSet cdSet = findContextDataSetAnnotation(fi.type);
                    if (cdSet == null) {
                        fi.isContextDataSet = false;
                    } else {
                        fi.isContextDataSet = true;
                    }

                    target.setFieldList.add(fi);

                    if (cd.reverse()) {//
                        if (reverseTargetScopes.contains(fi.scope)) {
                            target.getFieldList.add(fi);
                        } else {
                            String msg = String.format(
                                    "Only scope in [%s] can be marked as reverse injectable but found scope as %s (%s:%s).",
                                    reverseTargetScopes.toString(), fi.scope, cls.getName(), fi.name);
                            Asta4DWarningException awe = new Asta4DWarningException(msg);
                            logger.warn(msg, awe);
                        }
                    }

                }
            }
            cls = cls.getSuperclass();
        }

        return target;
    }

    /**
     * Retrieve value from {@link Context} for given Method by configured
     * {@link ContextDataFinder}
     * 
     * @param method
     *            given method
     * @return Retrieved values
     * @throws DataOperationException
     */
    public final static Object[] getMethodInjectParams(Method method) throws DataOperationException {
        ContextDataFinder dataFinder = Configuration.getConfiguration().getContextDataFinder();
        return getMethodInjectParams(method, dataFinder);
    }

    /**
     * Retrieve value from {@link Context} for given Method by given
     * {@link ContextDataFinder}
     * 
     * @param method
     *            given method
     * @param dataFinder
     *            given ContextDataFinder
     * @return Retrieved values
     * @throws DataOperationException
     */
    public final static Object[] getMethodInjectParams(Method method, ContextDataFinder dataFinder) throws DataOperationException {
        List<TargetInfo> targetList = getMethodTarget(method);
        Object[] params = new Object[targetList.size()];
        if (params.length == 0) {
            return params;
        }

        Context context = Context.getCurrentThreadContext();

        TargetInfo target;

        @SuppressWarnings("rawtypes")
        ContextDataHolder holder;

        try {
            for (int i = 0; i < params.length; i++) {
                target = targetList.get(i);
                if (target.isContextDataSet) {
                    Object obj = target.type.newInstance();
                    injectToInstance(obj);
                    params[i] = obj;
                } else {
                    // TODO allow log output for search information from
                    // ContextDataHolder
                    holder = dataFinder.findDataInContext(context, target.scope, target.name, target.type);
                    params[i] = holder == null ? null : holder.getValue();
                }
            }
        } catch (InstantiationException | IllegalAccessException e) {
            throw new DataOperationException("create instance failed.", e);
        }

        return params;
    }

    private final static List<TargetInfo> getMethodTarget(Method method) {
        boolean cacheEnable = Configuration.getConfiguration().isCacheEnable();
        List<TargetInfo> targetList = null;
        if (cacheEnable) {
            targetList = MethodTargetCache.get(method);
            if (targetList == null) {
                targetList = createMethodTarget(method);
                MethodTargetCache.put(method, targetList);
            }
        } else {
            targetList = createMethodTarget(method);
        }
        return targetList;
    }

    private final static List<TargetInfo> createMethodTarget(Method method) {
        Class<?>[] types = method.getParameterTypes();
        List<TargetInfo> targetList = new ArrayList<>();
        if (types.length == 0) {
            return targetList;
        }
        Annotation[][] annotations = method.getParameterAnnotations();
        String[] parameterNames = paranamer.lookupParameterNames(method);
        TargetInfo target;
        ContextData cd;
        ContextDataSet cdSet;
        for (int i = 0; i < types.length; i++) {
            target = new TargetInfo();
            target.type = types[i];

            cd = findContextDataAnnotation(annotations[i]);
            cdSet = findContextDataSetAnnotation(target.type);
            target.name = cd == null ? "" : cd.name();
            target.scope = cd == null ? "" : cd.scope();
            if (StringUtils.isEmpty(target.name)) {
                target.name = parameterNames[i];
            }

            if (cdSet == null) {
                target.isContextDataSet = false;
            } else {
                target.isContextDataSet = true;
            }

            target.fixForPrimitiveType();
            targetList.add(target);
        }
        return targetList;
    }

    private final static ContextData findContextDataAnnotation(Annotation[] annotations) {
        ContextData cd = null;
        Class<?> cls;
        for (Annotation annotation : annotations) {
            cls = annotation.annotationType();
            if (cls.equals(ContextData.class)) {
                cd = (ContextData) annotation;
                break;
            } else if (cls.isAnnotationPresent(ContextData.class)) {
                cd = cls.getAnnotation(ContextData.class);
                break;
            }
        }
        return cd;
    }

    private final static ContextDataSet findContextDataSetAnnotation(Class<?> type) {
        ContextDataSet cdset = null;
        Class<?> c = type;
        while (c != null) {
            cdset = iterateContextDataSetAnnotation(c.getAnnotations());
            if (cdset == null) {
                Class<?>[] ifs = c.getInterfaces();
                for (Class<?> ifcls : ifs) {
                    cdset = findContextDataSetAnnotation(ifcls);
                    if (cdset != null) {
                        break;
                    }
                }// for ifcls
            }// cdset == null
            if (cdset != null) {
                break;
            }
            c = c.getSuperclass();
        }// while parent != null
        return cdset;
    }

    private final static ContextDataSet iterateContextDataSetAnnotation(Annotation[] annotations) {
        ContextDataSet cdset = null;

        Class<?> cls;
        for (Annotation annotation : annotations) {
            cls = annotation.annotationType();
            if (cls.equals(ContextDataSet.class)) {
                cdset = (ContextDataSet) annotation;
                break;
            } else if (cls.isAnnotationPresent(ContextDataSet.class)) {
                cdset = cls.getAnnotation(ContextDataSet.class);
                break;
            }
        }
        return cdset;
    }

}
