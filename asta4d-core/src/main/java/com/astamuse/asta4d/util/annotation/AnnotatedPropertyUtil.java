/*
 * Copyright 2014 astamuse company,Ltd.
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
package com.astamuse.asta4d.util.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.astamuse.asta4d.Configuration;
import com.astamuse.asta4d.util.ClassUtil;

public class AnnotatedPropertyUtil {

    private static class ReadOnlyAnnotatedPropertyInfo extends AnnotatedPropertyInfo {
        private AnnotatedPropertyInfo info;

        ReadOnlyAnnotatedPropertyInfo(AnnotatedPropertyInfo info) {
            this.info = info;
        }

        public String getName() {
            return info.getName();
        }

        public void setName(String name) {
            throw new UnsupportedOperationException();
        }

        public String getBeanPropertyName() {
            return info.getBeanPropertyName();
        }

        public void setBeanPropertyName(String beanPropertyName) {
            throw new UnsupportedOperationException();
        }

        public Field getField() {
            return info.getField();
        }

        public void setField(Field field) {
            throw new UnsupportedOperationException();
        }

        public Method getGetter() {
            return info.getGetter();
        }

        public void setGetter(Method getter) {
            throw new UnsupportedOperationException();
        }

        public Method getSetter() {
            return info.getSetter();
        }

        public void setSetter(Method setter) {
            throw new UnsupportedOperationException();
        }

        @SuppressWarnings("rawtypes")
        public Class getType() {
            return info.getType();
        }

        @SuppressWarnings("rawtypes")
        public void setType(Class type) {
            throw new UnsupportedOperationException();
        }

        public <A extends Annotation> A getAnnotation(Class<A> annotationCls) {
            return info.getAnnotation(annotationCls);
        }

        public void setAnnotations(List<Annotation> annotationList) {
            throw new UnsupportedOperationException();
        }

        public void assignValue(Object instance, Object value)
                throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
            info.assignValue(instance, value);
        }

        public Object retrieveValue(Object instance) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
            return info.retrieveValue(instance);
        }

        public int hashCode() {
            return info.hashCode();
        }

        public boolean equals(Object obj) {
            return info.equals(obj);
        }

        public String toString() {
            return info.toString();
        }

    }

    private static class AnnotatedPropertyInfoMap {
        Map<String, List<AnnotatedPropertyInfo>> nameMap;
        Map<String, List<AnnotatedPropertyInfo>> beanNameMap;
        List<AnnotatedPropertyInfo> list;

        AnnotatedPropertyInfoMap(List<AnnotatedPropertyInfo> infoList) {
            list = infoList.stream().map(info -> new ReadOnlyAnnotatedPropertyInfo(info)).collect(Collectors.toList());
            list = Collections.unmodifiableList(list);

            nameMap = list.stream().collect(Collectors.groupingBy(info -> info.getName()));
            makeListUnmodifiable(nameMap);
            nameMap = Collections.unmodifiableMap(nameMap);

            beanNameMap = list.stream().collect(Collectors.groupingBy(info -> info.getBeanPropertyName()));
            makeListUnmodifiable(beanNameMap);
            beanNameMap = Collections.unmodifiableMap(beanNameMap);
        }

        void makeListUnmodifiable(Map<String, List<AnnotatedPropertyInfo>> map) {
            for (String key : map.keySet()) {
                map.put(key, Collections.unmodifiableList(map.get(key)));
            }
        }

    }

    private static final Logger logger = LoggerFactory.getLogger(AnnotatedPropertyUtil.class);

    private static final ConcurrentHashMap<String, AnnotatedPropertyInfoMap> propertiesMapCache = new ConcurrentHashMap<>();

    // TODO allow method property to override field property to avoid duplicated properties
    @SuppressWarnings({ "rawtypes", "unchecked" })
    private static AnnotatedPropertyInfoMap retrievePropertiesMap(Class cls) {
        String cacheKey = cls.getName();
        AnnotatedPropertyInfoMap map = propertiesMapCache.get(cacheKey);
        if (map == null) {
            List<AnnotatedPropertyInfo> infoList = new LinkedList<>();
            Set<String> beanPropertyNameSet = new HashSet<>();

            Method[] mtds = cls.getMethods();
            for (Method method : mtds) {
                List<Annotation> annoList = ConvertableAnnotationRetriever.retrieveAnnotationHierarchyList(AnnotatedProperty.class,
                        method.getAnnotations());

                if (CollectionUtils.isEmpty(annoList)) {
                    continue;
                }

                AnnotatedPropertyInfo info = new AnnotatedPropertyInfo();
                info.setAnnotations(annoList);

                boolean isGet = false;
                boolean isSet = false;
                String propertySuffixe = method.getName();
                if (propertySuffixe.startsWith("set")) {
                    propertySuffixe = propertySuffixe.substring(3);
                    isSet = true;
                } else if (propertySuffixe.startsWith("get")) {
                    propertySuffixe = propertySuffixe.substring(3);
                    isGet = true;
                } else if (propertySuffixe.startsWith("is")) {
                    propertySuffixe = propertySuffixe.substring(2);
                    isSet = true;
                } else {
                    String msg = String.format("Method [%s]:[%s] can not be treated as a getter or setter method.", cls.getName(),
                            method.toGenericString());
                    throw new RuntimeException(msg);
                }

                char[] cs = propertySuffixe.toCharArray();
                cs[0] = Character.toLowerCase(cs[0]);
                info.setBeanPropertyName(new String(cs));

                AnnotatedProperty ap = (AnnotatedProperty) annoList.get(0);// must by
                String name = ap.name();
                if (StringUtils.isEmpty(name)) {
                    name = info.getBeanPropertyName();
                }

                info.setName(name);

                if (isGet) {
                    info.setGetter(method);
                    info.setType(method.getReturnType());
                    String setterName = "set" + propertySuffixe;
                    Method setter = null;
                    try {
                        setter = cls.getMethod(setterName, method.getReturnType());
                    } catch (NoSuchMethodException | SecurityException e) {
                        String msg = "Could not find setter method:[{}({})] in class[{}] for annotated getter:[{}]";
                        logger.warn(msg, new Object[] { setterName, method.getReturnType().getName(), cls.getName(), method.getName() });
                    }
                    info.setSetter(setter);
                }

                if (isSet) {
                    info.setSetter(method);
                    info.setType(method.getParameterTypes()[0]);
                    String getterName = "get" + propertySuffixe;
                    Method getter = null;
                    try {
                        getter = cls.getMethod(getterName);
                    } catch (NoSuchMethodException | SecurityException e) {
                        String msg = "Could not find getter method:[{}:{}] in class[{}] for annotated setter:[{}]";
                        logger.warn(msg, new Object[] { getterName, method.getReturnType().getName(), cls.getName(), method.getName() });
                    }
                    info.setGetter(getter);
                }

                infoList.add(info);
                beanPropertyNameSet.add(info.getBeanPropertyName());
            }

            List<Field> list = new ArrayList<>(ClassUtil.retrieveAllFieldsIncludeAllSuperClasses(cls));
            Iterator<Field> it = list.iterator();

            while (it.hasNext()) {
                Field f = it.next();
                List<Annotation> annoList = ConvertableAnnotationRetriever.retrieveAnnotationHierarchyList(AnnotatedProperty.class,
                        f.getAnnotations());
                if (CollectionUtils.isNotEmpty(annoList)) {
                    AnnotatedProperty ap = (AnnotatedProperty) annoList.get(0);// must by

                    String beanPropertyName = f.getName();
                    if (beanPropertyNameSet.contains(beanPropertyName)) {
                        continue;
                    }

                    String name = ap.name();
                    if (StringUtils.isEmpty(name)) {
                        name = f.getName();
                    }

                    AnnotatedPropertyInfo info = new AnnotatedPropertyInfo();
                    info.setAnnotations(annoList);
                    info.setBeanPropertyName(beanPropertyName);
                    info.setName(name);
                    info.setField(f);
                    info.setGetter(null);
                    info.setSetter(null);
                    info.setType(f.getType());
                    infoList.add(info);
                }
            }

            map = new AnnotatedPropertyInfoMap(infoList);
            if (Configuration.getConfiguration().isCacheEnable()) {
                propertiesMapCache.put(cacheKey, map);
            }
        }
        return map;
    }

    @SuppressWarnings("rawtypes")
    public static List<AnnotatedPropertyInfo> retrieveProperties(Class cls) {
        AnnotatedPropertyInfoMap map = retrievePropertiesMap(cls);
        return map.list;
    }

    @SuppressWarnings("rawtypes")
    public static List<AnnotatedPropertyInfo> retrievePropertyByName(Class cls, final String name) {
        AnnotatedPropertyInfoMap map = retrievePropertiesMap(cls);
        return map.nameMap.get(name);
    }

    @SuppressWarnings("rawtypes")
    public static List<AnnotatedPropertyInfo> retrievePropertyByBeanPropertyName(Class cls, final String name) {
        AnnotatedPropertyInfoMap map = retrievePropertiesMap(cls);
        return map.beanNameMap.get(name);
    }

    public static void assignValueByName(Object instance, String name, Object value)
            throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        List<AnnotatedPropertyInfo> list = retrievePropertyByName(instance.getClass(), name);
        assignValue(list, instance, value);
    }

    public static void assignValueByBeanPropertyName(Object instance, String name, Object value)
            throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        List<AnnotatedPropertyInfo> list = retrievePropertyByBeanPropertyName(instance.getClass(), name);
        assignValue(list, instance, value);
    }

    public static void assignValue(List<AnnotatedPropertyInfo> list, Object instance, Object value)
            throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        for (AnnotatedPropertyInfo p : list) {
            p.assignValue(instance, value);
        }
    }
}
