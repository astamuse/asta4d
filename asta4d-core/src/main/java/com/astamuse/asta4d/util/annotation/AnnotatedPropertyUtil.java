package com.astamuse.asta4d.util.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.astamuse.asta4d.data.DataOperationException;
import com.astamuse.asta4d.util.ClassUtil;

public class AnnotatedPropertyUtil {

    private static final Logger logger = LoggerFactory.getLogger(AnnotatedPropertyUtil.class);

    // TODO allow method property to override field property to avoid duplicated properties

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static <A extends Annotation> List<AnnotatedPropertyInfo<A>> retrieveProperties(Class cls, Class<A> annotationCls)
            throws DataOperationException {

        List<AnnotatedPropertyInfo<A>> infoList = new LinkedList<>();

        List<Field> list = new ArrayList<>(ClassUtil.retrieveAllFieldsIncludeAllSuperClasses(cls));
        Iterator<Field> it = list.iterator();

        while (it.hasNext()) {
            Field f = it.next();
            A anno = ConvertableAnnotationRetriever.retrieveAnnotation(annotationCls, f.getAnnotations());
            if (anno != null) {
                AnnotatedPropertyInfo<A> info = new AnnotatedPropertyInfo<>();
                info.setAnnotation(anno);
                info.setName(f.getName());
                info.setField(f);
                info.setGetter(null);
                info.setSetter(null);
                info.setType(f.getType());
                infoList.add(info);
            }
        }

        Method[] mtds = cls.getMethods();
        for (Method method : mtds) {
            A anno = ConvertableAnnotationRetriever.retrieveAnnotation(annotationCls, method.getAnnotations());

            if (anno == null) {
                continue;
            }

            AnnotatedPropertyInfo<A> info = new AnnotatedPropertyInfo<>();
            info.setAnnotation(anno);

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
                throw new DataOperationException(msg);
            }

            char[] cs = propertySuffixe.toCharArray();
            cs[0] = Character.toLowerCase(cs[0]);
            info.setName(new String(cs));

            if (isGet) {
                info.setGetter(method);
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
                String getterName = "get" + propertySuffixe;
                Method getter = null;
                try {
                    getter = cls.getMethod(getterName, method.getReturnType());
                } catch (NoSuchMethodException | SecurityException e) {
                    String msg = "Could not find getter method:[{}:{}] in class[{}] for annotated setter:[{}]";
                    logger.warn(msg, new Object[] { getterName, method.getReturnType().getName(), cls.getName(), method.getName() });
                }
                info.setGetter(getter);
            }

            info.setType(info.getGetter().getReturnType());

            infoList.add(info);
        }

        return infoList;
    }

}
