package com.astamuse.asta4d.web.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import javax.servlet.ServletContext;

public class BinaryDataUtil {

    public final static InputStream retrieveInputStreamByPath(ServletContext servletContext, ClassLoader classLoader, String path) {
        if (path.startsWith("file://")) {
            try {
                return new FileInputStream(path);
            } catch (FileNotFoundException e) {
                return null;
            }
        } else if (path.startsWith("classpath:")) {
            String cls = path.substring("classpath".length());
            return classLoader.getResourceAsStream(cls);
        } else {
            return servletContext.getResourceAsStream(path);
        }
    }

}
