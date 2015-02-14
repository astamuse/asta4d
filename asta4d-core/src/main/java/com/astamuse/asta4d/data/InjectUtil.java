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
import java.util.HashMap;
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
import com.astamuse.asta4d.util.annotation.AnnotatedPropertyInfo;
import com.astamuse.asta4d.util.annotation.AnnotatedPropertyUtil;
import com.astamuse.asta4d.util.annotation.ConvertableAnnotationRetriever;
import com.thoughtworks.paranamer.AdaptiveParanamer;
import com.thoughtworks.paranamer.Paranamer;

/**
 * This class is a function holder to supply functionalities about data injection
 * 
 * @author e-ryu
 * 
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class InjectUtil {

    private final static Logger logger = LoggerFactory.getLogger(InjectUtil.class);

    private static final String ContextDataSetSingletonMapKey = "ContextDataSetSingletonMapKey#" + InjectUtil.class.getName();

    public static final String ContextDataNotFoundScope = "#ContextDataNotFoundScope";
    public static final String ContextDataTypeUnMatchScope = "#ContextDataTypeUnMatchScope";

    /**
     * A class that present the injectable target information
     * 
     * @author e-ryu
     * 
     */
    private static class TargetInfo {
        AnnotatedPropertyInfo propertyInfo;
        String name;
        String scope;
        TypeUnMacthPolicy typeUnMatch;
        ContextDataSetFactory contextDataSetFactory;
        boolean isContextDataSetSingletonInContext;
        Class<?> type;
        boolean isContextDataHolder;
        Object defaultValue;

        void fixForPrimitiveType() {
            TypeInfo typeInfo = new TypeInfo(type);
            type = typeInfo.getType();
            defaultValue = typeInfo.getDefaultValue();
        }

        ContextDataHolder createDataHolderInstance() throws InstantiationException, IllegalAccessException {
            if (isContextDataHolder) {
                return (ContextDataHolder) type.newInstance();
            } else {
                return new ContextDataHolder(type);
            }
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

    public final static Object retrieveContextDataSetInstance(Class cls, String searchName, String searchScope)
            throws DataOperationException {
        try {
            ContextDataSet cds = ConvertableAnnotationRetriever.retrieveAnnotation(ContextDataSet.class, cls.getAnnotations());
            if (cds == null) {
                throw new NullPointerException("Could not find any ContextDataSet convertable annotation on given class:" + cls.getName());
            }
            TargetInfo info = new TargetInfo();
            info.contextDataSetFactory = cds.factory().newInstance();
            info.isContextDataSetSingletonInContext = cds.singletonInContext();
            info.defaultValue = null;
            info.name = searchName;
            info.scope = searchScope;
            info.type = cls;
            info.typeUnMatch = TypeUnMacthPolicy.EXCEPTION;
            ContextDataHolder result = findValueForTarget(info, null);
            if (result == null) {
                return null;
            } else {
                return result.getValue();
            }
        } catch (IllegalAccessException | InstantiationException e) {
            throw new DataOperationException(e.getMessage(), e);
        }
    }

    /**
     * Set the value of all the fields marked by {@link ContextData} of the given instance.
     * 
     * @param instance
     * @throws DataOperationException
     */
    public final static void injectToInstance(Object instance) throws DataOperationException {

        InstanceWireTarget target = getInstanceTarget(instance);

        for (FieldInfo fi : target.setFieldList) {
            try {
                ContextDataHolder valueHolder = null;
                if (fi.isContextDataHolder) {
                    valueHolder = (ContextDataHolder) FieldUtils.readField(fi.field, instance);
                }
                if (valueHolder == null) {
                    valueHolder = fi.createDataHolderInstance();
                }
                Class searchType = valueHolder.getTypeCls();
                if (searchType == null) {
                    throw new DataOperationException(
                            fi.field.getName() +
                                    " should be initialized at first or we can not retrieve the type you want since it is a type of CotnextDataHolder. " +
                                    "You can also define an extended class to return the type class, in this case, you do not need to initialized it by your self");
                }
                ContextDataHolder foundData = findValueForTarget(fi, searchType);

                handleTypeUnMatch(instance, fi, foundData);

                if (fi.isContextDataHolder) {
                    transferDataHolder(foundData, valueHolder);
                    FieldUtils.writeField(fi.field, instance, valueHolder, true);
                } else {
                    FieldUtils.writeField(fi.field, instance, foundData.getValue(), true);
                }
            } catch (IllegalAccessException | IllegalArgumentException | InstantiationException e) {
                throw new DataOperationException("Exception when inject value to " + fi.field.toString(), e);
            }

        }

        for (MethodInfo mi : target.setMethodList) {
            try {
                ContextDataHolder valueHolder = mi.createDataHolderInstance();
                Class searchType = valueHolder.getTypeCls();
                if (searchType == null) {
                    throw new DataOperationException(mi.method.getName() + " cannot initialize an instance of " +
                            valueHolder.getClass().getName() + ". You should define an extended class to return the type class");
                }
                ContextDataHolder foundData = findValueForTarget(mi, searchType);
                handleTypeUnMatch(instance, mi, foundData);
                if (mi.isContextDataHolder) {
                    transferDataHolder(foundData, valueHolder);
                    mi.method.invoke(instance, valueHolder);
                } else {
                    mi.method.invoke(instance, foundData.getValue());
                }
            } catch (IllegalAccessException | IllegalArgumentException | InstantiationException | InvocationTargetException e) {
                throw new DataOperationException("Exception when inject value to " + mi.method.toString(), e);
            }
        }

    }

    private static void handleTypeUnMatch(Object instance, FieldInfo target, ContextDataHolder valueHolder) throws DataOperationException {
        handleTypeUnMatch(instance, target.field, null, null, -1, target, valueHolder);
    }

    private static void handleTypeUnMatch(Object instance, MethodInfo target, ContextDataHolder valueHolder) throws DataOperationException {
        handleTypeUnMatch(instance, null, target.method, null, -1, target, valueHolder);
    }

    private static void handleTypeUnMatch(Method method, int methodParameterIndex, TargetInfo target, ContextDataHolder valueHolder)
            throws DataOperationException {
        handleTypeUnMatch(null, null, null, method, methodParameterIndex, target, valueHolder);
    }

    private static void handleTypeUnMatch(Object instance, Field field, Method setter, Method method, int methodParameterIndex,
            TargetInfo target, ContextDataHolder valueHolder) throws DataOperationException {
        // type unmatched
        if (ContextDataTypeUnMatchScope.equals(valueHolder.getScope())) {
            switch (target.typeUnMatch) {
            case EXCEPTION:
                String msg = "Found data(%s) cannot be coverted from [%s] to [%s].";
                msg = String.format(msg, valueHolder.getFoundOriginalData(), valueHolder.getFoundOriginalData().getClass(), target.type);
                throw new DataOperationException(msg);
            case DEFAULT_VALUE:
                valueHolder.setData(valueHolder.getName(), valueHolder.getScope(), target.defaultValue);
                break;
            case DEFAULT_VALUE_AND_TRACE:
                ContextDataHolder traceHolder = new ContextDataHolder();
                transferDataHolder(valueHolder, traceHolder);
                if (field != null) {
                    InjectTrace.saveInstanceInjectionTraceInfo(instance, field, traceHolder);
                } else if (setter != null) {
                    InjectTrace.saveInstanceInjectionTraceInfo(instance, setter, traceHolder);
                } else if (method != null) {
                    InjectTrace.saveMethodInjectionTraceInfo(method, methodParameterIndex, traceHolder);
                }
                valueHolder.setData(valueHolder.getName(), valueHolder.getScope(), target.defaultValue);
                break;
            }
        }
    }

    private static void transferDataHolder(ContextDataHolder from, ContextDataHolder to) {
        to.setData(from.getName(), from.getScope(), from.getFoundOriginalData(), from.getValue());
    }

    private static ContextDataHolder findValueForTarget(TargetInfo targetInfo, Class overrideSearchType) throws DataOperationException {
        Context context = Context.getCurrentThreadContext();
        ContextDataFinder dataFinder = Configuration.getConfiguration().getContextDataFinder();

        Class searchType = overrideSearchType == null ? targetInfo.type : overrideSearchType;
        ContextDataHolder valueHolder = dataFinder.findDataInContext(context, targetInfo.scope, targetInfo.name, searchType);
        if (valueHolder == null && targetInfo.contextDataSetFactory != null) {
            Object value;
            if (targetInfo.isContextDataSetSingletonInContext) {
                // this map was initialized when the context was initialized
                HashMap<String, Object> cdSetSingletonMap = context.getData(ContextDataSetSingletonMapKey);
                // we must synchronize it to avoid concurrent access on the map
                synchronized (cdSetSingletonMap) {
                    String clsName = targetInfo.type.getName();
                    value = cdSetSingletonMap.get(clsName);
                    if (value == null) {
                        value = targetInfo.contextDataSetFactory.createInstance(targetInfo.type);
                        injectToInstance(value);
                        cdSetSingletonMap.put(clsName, value);
                    }
                }
            } else {
                value = targetInfo.contextDataSetFactory.createInstance(targetInfo.type);
                injectToInstance(value);
            }

            valueHolder = new ContextDataHolder(targetInfo.name, targetInfo.scope, value);
        } else if (valueHolder == null) {
            valueHolder = new ContextDataHolder(targetInfo.name, ContextDataNotFoundScope, targetInfo.defaultValue);
        }
        return valueHolder;

    }

    /**
     * Retrieve values from fields marked as reverse injectable of given instance.
     * 
     * There are only limited scopes can be marked as injectable. See {@link Configuration#setReverseInjectableScopes(List)}.
     * 
     * NOT SUPPORTED ANY MORE!!!
     * 
     * @param instance
     * @throws DataOperationException
     */
    @Deprecated
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

        InstanceWireTarget target = new InstanceWireTarget();
        Class<?> cls = instance.getClass();

        List<AnnotatedPropertyInfo> propertyList = AnnotatedPropertyUtil.retrieveProperties(cls);

        try {
            for (AnnotatedPropertyInfo prop : propertyList) {
                ContextData cd = prop.getAnnotation(ContextData.class);

                if (prop.getField() != null) {
                    Field field = prop.getField();
                    FieldInfo fi = new FieldInfo();
                    fi.propertyInfo = prop;
                    fi.name = prop.getName();
                    fi.field = field;
                    fi.type = field.getType();
                    fi.isContextDataHolder = ContextDataHolder.class.isAssignableFrom(fi.type);

                    fi.scope = cd.scope();
                    fi.typeUnMatch = cd.typeUnMatch();
                    fi.fixForPrimitiveType();

                    ContextDataSet cdSet = ConvertableAnnotationRetriever
                            .retrieveAnnotation(ContextDataSet.class, fi.type.getAnnotations());
                    if (cdSet == null) {
                        fi.contextDataSetFactory = null;
                    } else {
                        fi.contextDataSetFactory = cdSet.factory().newInstance();
                        fi.isContextDataSetSingletonInContext = cdSet.singletonInContext();
                    }

                    target.setFieldList.add(fi);

                } else {// for method
                    MethodInfo mi = new MethodInfo();
                    mi.propertyInfo = prop;
                    mi.name = prop.getName();
                    mi.method = prop.getSetter();
                    if (mi.method == null) {
                        throw new DataOperationException("Could not find setter method for annotated property:" + prop.getName());
                    }
                    mi.type = mi.method.getParameterTypes()[0];
                    mi.isContextDataHolder = ContextDataHolder.class.isAssignableFrom(mi.type);

                    mi.scope = cd.scope();
                    mi.typeUnMatch = cd.typeUnMatch();
                    mi.fixForPrimitiveType();

                    ContextDataSet cdSet = ConvertableAnnotationRetriever
                            .retrieveAnnotation(ContextDataSet.class, mi.type.getAnnotations());
                    if (cdSet == null) {
                        mi.contextDataSetFactory = null;
                    } else {
                        mi.contextDataSetFactory = cdSet.factory().newInstance();
                        mi.isContextDataSetSingletonInContext = cdSet.singletonInContext();
                    }
                    target.setMethodList.add(mi);

                }

            }
        } catch (InstantiationException | IllegalAccessException e) {
            throw new DataOperationException("Exception occured on generating injection information of " + instance.getClass(), e);
        }

        return target;
    }

    /**
     * Retrieve value from {@link Context} for given Method by configured {@link ContextDataFinder}
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
     * Retrieve value from {@link Context} for given Method by given {@link ContextDataFinder}
     * 
     * @param method
     *            given method
     * @param dataFinder
     *            given ContextDataFinder
     * @return Retrieved values
     * @throws DataOperationException
     */
    public final static Object[] getMethodInjectParams(Method method, ContextDataFinder dataFinder) throws DataOperationException {
        try {
            List<TargetInfo> targetList = getMethodTarget(method);
            Object[] params = new Object[targetList.size()];
            if (params.length == 0) {
                return params;
            }

            TargetInfo target;

            ContextDataHolder valueHolder, foundData;

            Class searchType;

            for (int i = 0; i < params.length; i++) {
                target = targetList.get(i);

                valueHolder = target.createDataHolderInstance();
                searchType = valueHolder.getTypeCls();
                if (searchType == null) {
                    throw new DataOperationException(method.getName() + " cannot initialize an instance of " +
                            valueHolder.getClass().getName() + ". You should define an extended class to return the type class");
                }
                foundData = findValueForTarget(target, searchType);
                handleTypeUnMatch(method, i, target, foundData);

                if (target.isContextDataHolder) {
                    transferDataHolder(foundData, valueHolder);
                    params[i] = valueHolder;
                } else {
                    params[i] = foundData.getValue();
                }

            }

            return params;
        } catch (InstantiationException | IllegalAccessException e) {
            throw new DataOperationException("create instance failed.", e);
        }
    }

    private final static List<TargetInfo> getMethodTarget(Method method) throws InstantiationException, IllegalAccessException {
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

    private final static List<TargetInfo> createMethodTarget(Method method) throws InstantiationException, IllegalAccessException {
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
            target.isContextDataHolder = ContextDataHolder.class.isAssignableFrom(target.type);

            cd = ConvertableAnnotationRetriever.retrieveAnnotation(ContextData.class, annotations[i]);
            cdSet = ConvertableAnnotationRetriever.retrieveAnnotation(ContextDataSet.class, target.type.getAnnotations());
            target.name = cd == null ? "" : cd.name();
            target.scope = cd == null ? "" : cd.scope();
            target.typeUnMatch = cd == null ? TypeUnMacthPolicy.EXCEPTION : cd.typeUnMatch();
            if (StringUtils.isEmpty(target.name)) {
                target.name = parameterNames[i];
            }

            if (cdSet == null) {
                target.contextDataSetFactory = null;
            } else {
                target.contextDataSetFactory = cdSet.factory().newInstance();
                target.isContextDataSetSingletonInContext = cdSet.singletonInContext();
            }

            target.fixForPrimitiveType();
            targetList.add(target);
        }
        return targetList;
    }

    public static final void initContext(Context context) {
        context.setData(ContextDataSetSingletonMapKey, new HashMap());
    }
}
