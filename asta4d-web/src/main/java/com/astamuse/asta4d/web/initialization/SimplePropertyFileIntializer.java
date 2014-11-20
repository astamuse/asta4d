package com.astamuse.asta4d.web.initialization;

import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.Enumeration;
import java.util.Properties;

import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.ConvertUtilsBean;
import org.apache.commons.beanutils.PropertyUtils;

import com.astamuse.asta4d.web.WebApplicationConfiguration;

public class SimplePropertyFileIntializer implements Initializer {

    @Override
    public void initliaze(InputStream input, WebApplicationConfiguration configuration) throws Exception {
        Properties ps = new Properties();
        ps.load(input);

        BeanUtilsBean bu = retrieveBeanUtilsBean();
        Enumeration<Object> keys = ps.keys();
        while (keys.hasMoreElements()) {
            String key = keys.nextElement().toString();
            String value = ps.getProperty(key);
            fillConfiguration(configuration, bu, key, value);
        }

    }

    protected BeanUtilsBean retrieveBeanUtilsBean() {
        BeanUtilsBean bu = new BeanUtilsBean(new ConvertUtilsBean() {

            @SuppressWarnings({ "rawtypes", "unchecked" })
            @Override
            public Object convert(String value, Class clazz) {
                if (clazz.isEnum()) {
                    return Enum.valueOf(clazz, value);
                } else if (clazz.equals(Class.class)) {
                    try {
                        return Class.forName(value);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                } else if (expectInstance(clazz)) {
                    try {
                        return Class.forName(value).newInstance();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    return super.convert(value, clazz);
                }
            }

        });
        return bu;
    }

    @SuppressWarnings("rawtypes")
    protected boolean expectInstance(Class clz) {
        Package pkg = clz.getPackage();
        if (pkg == null) {
            return false;
        } else {
            return pkg.getName().startsWith("com.astamuse.asta4d.");
        }
    }

    @SuppressWarnings("rawtypes")
    protected void fillConfiguration(WebApplicationConfiguration conf, BeanUtilsBean beanUtil, String key, String value)
            throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        Class cls = PropertyUtils.getPropertyType(conf, key);
        if (cls.isArray()) {
            String[] values = value.split(",");
            beanUtil.setProperty(conf, key, values);
        } else {
            beanUtil.setProperty(conf, key, value);
        }
    }

}
