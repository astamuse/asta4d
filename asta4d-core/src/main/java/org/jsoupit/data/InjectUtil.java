package org.jsoupit.data;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.jsoupit.Context;
import org.jsoupit.data.annotation.ContextData;

import com.thoughtworks.paranamer.AdaptiveParanamer;
import com.thoughtworks.paranamer.Paranamer;

public class InjectUtil {
    private static class TargetInfo {
        String name;
        String scope;
        Class<?> type;
        Object defaultValue;

        void fixForPrimitiveType() {
            if (type.isPrimitive()) {
                String name = type.getName();
                switch (name) {
                case "char":
                    type = Character.class;
                    defaultValue = ' ';
                    break;
                case "byte":
                    type = Byte.class;
                    defaultValue = new Byte((byte) 0);
                    break;
                case "short":
                    type = Short.class;
                    defaultValue = new Short((short) 0);
                    break;
                case "int":
                    type = Integer.class;
                    defaultValue = new Integer(0);
                    break;
                case "long":
                    type = Long.class;
                    defaultValue = new Long(0L);
                    break;
                }
            } else {
                defaultValue = null;
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

    public final static void injectToInstance(Object instance) throws DataOperationException {
        try {

            Context context = Context.getCurrentThreadContext();
            InstanceWireTarget target = getInstanceTarget(instance);
            ContextDataFinder dataFinder = context.getConfiguration().getContextDataFinder();
            Object value;
            for (FieldInfo fi : target.setFieldList) {
                value = dataFinder.findDataInContext(context, fi.scope, fi.name, fi.type);
                FieldUtils.writeField(fi.field, instance, value, true);
            }

            for (MethodInfo mi : target.setMethodList) {
                value = dataFinder.findDataInContext(context, mi.scope, mi.name, mi.type);
                if (value == null && mi.defaultValue != null) {
                    value = mi.defaultValue;
                }
                mi.method.invoke(instance, value);
            }
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            throw new DataOperationException("Exception when inject value to instance of " + instance.getClass().toString(), e);
        }
    }

    /**
     * at present, we only reversely inject values which marked as request scope
     * 
     * @param instance
     * @throws DataOperationException
     */
    public final static void setContextDataFromInstance(Object instance) throws DataOperationException {
        try {
            Context context = Context.getCurrentThreadContext();
            InstanceWireTarget target = getInstanceTarget(instance);
            Object value;
            for (FieldInfo fi : target.setFieldList) {
                value = FieldUtils.readField(fi.field, instance, true);
                context.setData(fi.scope, fi.name, value);
            }

            for (MethodInfo mi : target.setMethodList) {
                value = mi.method.invoke(instance);
                context.setData(mi.scope, mi.name, value);
            }
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            String msg = String.format("Exception when inject value from instance of [%s] to Context.", instance.getClass().toString());
            throw new DataOperationException(msg, e);
        }
    }

    private final static InstanceWireTarget getInstanceTarget(Object instance) {
        boolean cacheEnable = Context.getCurrentThreadContext().getConfiguration().isCacheEnable();
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

    private final static InstanceWireTarget createInstanceTarget(Object instance) {
        List<String> reverseTargetScopes = Context.getCurrentThreadContext().getConfiguration().getReverseInjectableScopes();

        InstanceWireTarget target = new InstanceWireTarget();
        Class<?> cls = instance.getClass();
        ContextData cd;
        // TODO we should use class name to confirm contextdata annotation
        // because they are possibly from different class loader. The problem is
        // whether it is a problem?
        Method[] mtds = cls.getMethods();
        for (Method method : mtds) {
            if (method.isAnnotationPresent(ContextData.class)) {
                cd = method.getAnnotation(ContextData.class);
                MethodInfo mi = new MethodInfo();

                mi.method = method;

                boolean isGet = false;
                boolean isSet = false;
                if (cd.value().isEmpty()) {
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
                        if (method.getParameterTypes().length == 0) {
                            isGet = true;
                        } else {
                            isSet = true;
                        }
                    }
                    char[] cs = name.toCharArray();
                    cs[0] = Character.toLowerCase(cs[0]);
                    mi.name = new String(cs);

                } else {
                    mi.name = cd.value();
                    // TODO throw a exception if name is empty

                    int typeLength = method.getParameterTypes().length;
                    if (typeLength == 0) {
                        isGet = true;
                    } else if (typeLength == 1) {
                        isSet = true;
                    } else {
                        // TODO what we should do?
                    }
                }
                mi.scope = cd.scope();

                if (isGet) {
                    // only if the reverse value is explicitly set to true and
                    // the scope is contained in the allowing reverse injection
                    // list
                    if (cd.reverse() && reverseTargetScopes.contains(mi.scope)) {
                        mi.type = method.getReturnType();
                        mi.fixForPrimitiveType();
                        target.getMethodList.add(mi);
                    }
                }

                if (isSet) {
                    mi.type = method.getParameterTypes()[0];
                    mi.fixForPrimitiveType();
                    target.setMethodList.add(mi);
                }

            }
        }

        String objCls = Object.class.getName();
        Field[] flds;
        FieldInfo fi;
        while (!cls.getName().equals(objCls)) {
            flds = cls.getDeclaredFields();
            for (Field field : flds) {
                if (field.isAnnotationPresent(ContextData.class)) {
                    fi = new FieldInfo();
                    fi.field = field;
                    fi.type = field.getType();
                    cd = field.getAnnotation(ContextData.class);
                    if (cd.value().isEmpty()) {
                        fi.name = field.getName();
                    } else {
                        fi.name = cd.value();
                    }
                    fi.scope = cd.scope();
                    fi.fixForPrimitiveType();
                    target.setFieldList.add(fi);

                    if (cd.reverse() && reverseTargetScopes.contains(fi.scope)) {
                        target.getFieldList.add(fi);
                    }

                }
            }
            cls = cls.getSuperclass();
        }

        return target;
    }

    public final static Object[] getMethodInjectParams(Method method) {
        Context context = Context.getCurrentThreadContext();
        ContextDataFinder dataFinder = context.getConfiguration().getContextDataFinder();
        return getMethodInjectParams(method, dataFinder);
    }

    public final static Object[] getMethodInjectParams(Method method, ContextDataFinder dataFinder) {
        List<TargetInfo> targetList = getMethodTarget(method);
        Object[] params = new Object[targetList.size()];
        if (params.length == 0) {
            return params;
        }

        Context context = Context.getCurrentThreadContext();

        TargetInfo target;

        for (int i = 0; i < params.length; i++) {
            target = targetList.get(i);
            params[i] = dataFinder.findDataInContext(context, target.scope, target.name, target.type);
        }

        return params;
    }

    private final static List<TargetInfo> getMethodTarget(Method method) {
        boolean cacheEnable = Context.getCurrentThreadContext().getConfiguration().isCacheEnable();
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
        for (int i = 0; i < types.length; i++) {
            target = new TargetInfo();
            cd = findAnnotation(annotations[i]);
            target.name = cd == null ? "" : cd.value();
            target.scope = cd == null ? "" : cd.scope();
            if (StringUtils.isEmpty(target.name)) {
                target.name = parameterNames[i];
            }
            target.type = types[i];
            target.fixForPrimitiveType();
            targetList.add(target);
        }
        return targetList;
    }

    private final static ContextData findAnnotation(Annotation[] annotations) {
        ContextData cd = null;
        for (Annotation annotation : annotations) {
            if (annotation.annotationType().equals(ContextData.class)) {
                cd = (ContextData) annotation;
                break;
            }
        }
        return cd;
    }

}
