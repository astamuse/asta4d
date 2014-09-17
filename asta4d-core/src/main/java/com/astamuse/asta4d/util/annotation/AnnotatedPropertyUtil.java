package com.astamuse.asta4d.util.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.Predicate;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.astamuse.asta4d.util.ClassUtil;
import com.astamuse.asta4d.util.collection.ListConvertUtil;
import com.astamuse.asta4d.util.collection.RowConvertor;

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

        public void assginValue(Object instance, Object value) throws IllegalAccessException, IllegalArgumentException,
                InvocationTargetException {
            info.assginValue(instance, value);
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

    private static final Logger logger = LoggerFactory.getLogger(AnnotatedPropertyUtil.class);

    // TODO allow method property to override field property to avoid duplicated properties
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static List<AnnotatedPropertyInfo> retrieveProperties(Class cls) {

        List<AnnotatedPropertyInfo> infoList = new LinkedList<>();

        List<Field> list = new ArrayList<>(ClassUtil.retrieveAllFieldsIncludeAllSuperClasses(cls));
        Iterator<Field> it = list.iterator();

        while (it.hasNext()) {
            Field f = it.next();
            List<Annotation> annoList = ConvertableAnnotationRetriever.retrieveAnnotationHierarchyList(AnnotatedProperty.class,
                    f.getAnnotations());
            if (CollectionUtils.isNotEmpty(annoList)) {
                AnnotatedProperty ap = (AnnotatedProperty) annoList.get(0);// must by
                String name = ap.name();
                if (StringUtils.isEmpty(name)) {
                    name = f.getName();
                }

                AnnotatedPropertyInfo info = new AnnotatedPropertyInfo();
                info.setAnnotations(annoList);
                info.setBeanPropertyName(f.getName());
                info.setName(name);
                info.setField(f);
                info.setGetter(null);
                info.setSetter(null);
                info.setType(f.getType());
                infoList.add(info);
            }
        }

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
        }

        return ListConvertUtil.transform(infoList, new RowConvertor<AnnotatedPropertyInfo, AnnotatedPropertyInfo>() {

            @Override
            public AnnotatedPropertyInfo convert(int rowIndex, AnnotatedPropertyInfo info) {
                return new ReadOnlyAnnotatedPropertyInfo(info);
            }
        });
    }

    @SuppressWarnings("rawtypes")
    public static AnnotatedPropertyInfo retrievePropertyByName(Class cls, final String name) {
        List<AnnotatedPropertyInfo> list = retrieveProperties(cls);
        return CollectionUtils.find(list, new Predicate<AnnotatedPropertyInfo>() {
            @Override
            public boolean evaluate(AnnotatedPropertyInfo info) {
                return info.getName().equals(name);
            }
        });
    }

    @SuppressWarnings("rawtypes")
    public static AnnotatedPropertyInfo retrievePropertyByBeanPropertyName(Class cls, final String name) {
        List<AnnotatedPropertyInfo> list = retrieveProperties(cls);
        return CollectionUtils.find(list, new Predicate<AnnotatedPropertyInfo>() {
            @Override
            public boolean evaluate(AnnotatedPropertyInfo info) {
                return info.getBeanPropertyName().equals(name);
            }
        });
    }
}
