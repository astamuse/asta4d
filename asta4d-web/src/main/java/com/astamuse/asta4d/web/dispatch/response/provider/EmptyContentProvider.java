package com.astamuse.asta4d.web.dispatch.response.provider;

import com.astamuse.asta4d.web.dispatch.response.writer.ContentWriter;
import com.astamuse.asta4d.web.dispatch.response.writer.EmptyContentWriter;

public class EmptyContentProvider implements ContentProvider<Object> {

    @Override
    public boolean isContinuable() {
        return false;
    }

    @Override
    public Object produce() throws Exception {
        return null;
    }

    @Override
    public Class<? extends ContentWriter<Object>> getContentWriter() {
        return EmptyContentWriter.class;
    }

}
