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
package com.astamuse.asta4d.web.initialization;

import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;

import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.ConvertUtilsBean;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.configuration.PropertiesConfiguration;

import com.astamuse.asta4d.web.WebApplicationConfiguration;

public class SimplePropertyFileInitializer implements Initializer {

    @Override
    public void initliaze(InputStream input, WebApplicationConfiguration configuration) throws Exception {
        PropertiesConfiguration pc = new PropertiesConfiguration();
        pc.load(input);

        BeanUtilsBean bu = retrieveBeanUtilsBean();
        Iterator<String> keys = pc.getKeys();
        while (keys.hasNext()) {
            String key = keys.next();
            String value = pc.getString(key);
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
