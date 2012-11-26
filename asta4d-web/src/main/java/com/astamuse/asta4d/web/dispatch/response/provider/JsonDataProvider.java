package com.astamuse.asta4d.web.dispatch.response.provider;

import com.astamuse.asta4d.web.dispatch.annotation.ContentProvider;
import com.astamuse.asta4d.web.dispatch.response.JsonWriter;

public class JsonDataProvider {

    private Object data;

    public JsonDataProvider(Object data) {
        this.data = data;
    }

    @ContentProvider(writer = JsonWriter.class)
    public Object getData() {
        return data;
    }

}
