package com.astamuse.asta4d.web.dispatch.response.provider;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import javax.servlet.ServletContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import com.astamuse.asta4d.web.dispatch.mapping.UrlMappingRule;
import com.astamuse.asta4d.web.util.data.BinaryDataUtil;

public class BinaryDataProvider implements ContentProvider {

    private InputStream input = null;

    public BinaryDataProvider(InputStream input) {
        this.input = input;
    }

    public BinaryDataProvider(ServletContext servletContext, String inPackageFile) {
        this(servletContext.getResourceAsStream(inPackageFile));
    }

    public BinaryDataProvider(File commonFile) {
        this(retrieveInputStreamFromFile(commonFile));
    }

    public BinaryDataProvider(ServletContext servletContext, ClassLoader classLoader, String path) {
        this(BinaryDataUtil.retrieveInputStreamByPath(servletContext, classLoader, path));
    }

    private static final InputStream retrieveInputStreamFromFile(File file) {
        try {
            return new FileInputStream(file);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public BinaryDataProvider(byte[] bytes) {
        this(new ByteArrayInputStream(bytes));
    }

    @Override
    public boolean isContinuable() {
        return true;
    }

    @Override
    public void produce(UrlMappingRule currentRule, HttpServletResponse response) throws Exception {
        try {
            byte[] bs = new byte[4096];
            ServletOutputStream out = response.getOutputStream();
            int len = 0;
            while ((len = input.read(bs)) != -1) {
                out.write(bs, 0, len);
            }
            // we do not need to close servlet output stream since the container
            // will close it.
        } finally {
            // since we have depleted this stream, there is no reason for not
            // closing it.
            input.close();
        }
    }

}
