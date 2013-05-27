package com.astamuse.asta4d.web.dispatch.response.provider;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import javax.servlet.ServletContext;

import com.astamuse.asta4d.web.dispatch.response.writer.BinaryDataWriter;
import com.astamuse.asta4d.web.dispatch.response.writer.ContentWriter;

public class BinaryDataProvider implements ContentProvider<InputStream> {

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

    public BinaryDataProvider(String commonFilePath) {
        this(new File(commonFilePath));
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
    public InputStream produce() throws Exception {
        return input;
    }

    @Override
    public Class<? extends ContentWriter<InputStream>> getContentWriter() {
        return BinaryDataWriter.class;
    }

}
