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
package com.astamuse.asta4d.web.util;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletConfig;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SystemPropertyUtil {

    private final static Logger logger = LoggerFactory.getLogger(SystemPropertyUtil.class);

    public static enum PropertyScope {
        ServletConfig, JNDI, SystemProperty
    }

    public final static String retrievePropertyValue(String key, PropertyScope... scopes) {
        return retrievePropertyValue(null, key, scopes);

    }

    public final static String retrievePropertyValue(ServletConfig sc, String key, PropertyScope... scopes) {
        String v = null;
        for (PropertyScope scope : scopes) {
            switch (scope) {
            case ServletConfig:
                v = retrieveFromServletConfig(sc, key);
                break;
            case JNDI:
                v = retrieveFromJDNI(key);
                break;
            case SystemProperty:
                v = retrieveFromSystemProperty(key);
                break;
            }
            if (v == null) {
                logger.info("[{}] is not being configured in {}.", key, scope);
            } else {
                logger.info("[{}] is found in {} with value:[{}].", new Object[] { key, scope, v });
                break;
            }
        }
        return v;
    }

    private final static String retrieveFromServletConfig(ServletConfig sc, String key) {
        return sc.getInitParameter(key);
    }

    private final static String retrieveFromJDNI(String key) {
        InitialContext context = null;
        try {
            try {
                context = new InitialContext();
                return (String) context.lookup("java:comp/env/" + key);
            } finally {
                if (context != null) {
                    context.close();
                }
            }
        } catch (NamingException e) {
            return null;
        }
    }

    private final static String retrieveFromSystemProperty(String key) {
        return System.getProperty(key);
    }
}
