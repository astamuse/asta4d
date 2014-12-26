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
package com.astamuse.asta4d.util.i18n.pattern;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

public class CharsetResourceBundleFactory implements ResourceBundleFactory {

    private Charset charset;

    public CharsetResourceBundleFactory() {
        this("UTF-8");
    }

    public CharsetResourceBundleFactory(String charset) {
        this(Charset.forName(charset));
    }

    public CharsetResourceBundleFactory(Charset charset) {
        this.charset = charset;
    }

    public Charset getCharset() {
        return charset;
    }

    public void setCharset(Charset charset) {
        this.charset = charset;
    }

    public void setCharset(String charset) {
        this.charset = Charset.forName(charset);
    }

    protected ResourceBundle.Control createControl() {
        return new ResourceBundle.Control() {
            // this method is copied from the parent class
            @SuppressWarnings("unchecked")
            @Override
            public ResourceBundle newBundle(String baseName, Locale locale, String format, ClassLoader loader, boolean reload)
                    throws IllegalAccessException, InstantiationException, IOException {
                String bundleName = toBundleName(baseName, locale);
                ResourceBundle bundle = null;
                if (format.equals("java.class")) {
                    try {
                        Class<? extends ResourceBundle> bundleClass = (Class<? extends ResourceBundle>) loader.loadClass(bundleName);

                        // If the class isn't a ResourceBundle subclass, throw a
                        // ClassCastException.
                        if (ResourceBundle.class.isAssignableFrom(bundleClass)) {
                            bundle = bundleClass.newInstance();
                        } else {
                            throw new ClassCastException(bundleClass.getName() + " cannot be cast to ResourceBundle");
                        }
                    } catch (ClassNotFoundException e) {
                    }
                } else if (format.equals("java.properties")) {
                    final String resourceName = toResourceName(bundleName, "properties");
                    final ClassLoader classLoader = loader;
                    final boolean reloadFlag = reload;
                    InputStream stream = null;
                    try {
                        stream = AccessController.doPrivileged(new PrivilegedExceptionAction<InputStream>() {
                            public InputStream run() throws IOException {
                                InputStream is = null;
                                if (reloadFlag) {
                                    URL url = classLoader.getResource(resourceName);
                                    if (url != null) {
                                        URLConnection connection = url.openConnection();
                                        if (connection != null) {
                                            // Disable caches to get fresh data for
                                            // reloading.
                                            connection.setUseCaches(false);
                                            is = connection.getInputStream();
                                        }
                                    }
                                } else {
                                    is = classLoader.getResourceAsStream(resourceName);
                                }
                                return is;
                            }
                        });
                    } catch (PrivilegedActionException e) {
                        throw (IOException) e.getException();
                    }
                    if (stream != null) {
                        try {
                            // Only this line is changed to make it to read properties files as specified charset.
                            bundle = new PropertyResourceBundle(new InputStreamReader(stream, charset));
                        } finally {
                            stream.close();
                        }
                    }
                } else {
                    throw new IllegalArgumentException("unknown format: " + format);
                }
                return bundle;
            }

        };
    }

    @Override
    public ResourceBundle retrieveResourceBundle(String baseName, Locale locale) {
        return ResourceBundle.getBundle(baseName, locale == null ? Locale.getDefault() : locale, createControl());
    }

}
