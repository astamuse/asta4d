package com.astamuse.asta4d.web.dispatch.response.provider;

import com.astamuse.asta4d.web.dispatch.response.writer.ContentWriter;
import com.astamuse.asta4d.web.dispatch.response.writer.JsonWriter;

public class JsonDataProvider implements ContentProvider<Object> {

    private Object data;

    public JsonDataProvider() {
        this(null);
    }

    public JsonDataProvider(Object data) {
        this.data = data;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    @Override
    public boolean isContinuable() {
        return false;
    }

    @Override
    public Object produce() throws Exception {
        return data;
    }

    @Override
    public Class<? extends ContentWriter<Object>> getContentWriter() {
        return JsonWriter.class;
    }

}
