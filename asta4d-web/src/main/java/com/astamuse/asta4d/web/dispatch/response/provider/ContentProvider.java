package com.astamuse.asta4d.web.dispatch.response.provider;

import com.astamuse.asta4d.web.dispatch.response.writer.ContentWriter;

public interface ContentProvider<T> {

    public boolean isContinuable();

    public T produce() throws Exception;

    public Class<? extends ContentWriter<T>> getContentWriter();
}
