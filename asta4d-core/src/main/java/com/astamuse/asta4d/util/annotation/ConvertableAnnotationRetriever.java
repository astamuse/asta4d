package com.astamuse.asta4d.util.annotation;

import java.lang.annotation.Annotation;

public class ConvertableAnnotationRetriever {

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static final <T extends Annotation> T retrieveAnnotation(Class<T> targetAnnotation, Annotation... annotations) {

        try {
            String targetName = targetAnnotation.getName();
            T found = null;
            ConvertableAnnotation ca;
            for (Annotation annotation : annotations) {
                while (true) {
                    if (annotation.annotationType().getName().equals(targetName)) {
                        found = (T) annotation;
                        break;
                    }
                    ca = annotation.annotationType().getAnnotation(ConvertableAnnotation.class);
                    if (ca == null) {
                        break;
                    } else {
                        AnnotationConvertor ac = ca.value().newInstance();
                        annotation = ac.convert(annotation);
                    }
                }
                if (found != null) {
                    break;
                }
            }
            return found;
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
