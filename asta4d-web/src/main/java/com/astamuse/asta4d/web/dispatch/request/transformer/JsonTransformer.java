package com.astamuse.asta4d.web.dispatch.request.transformer;

import com.astamuse.asta4d.web.dispatch.request.ResultTransformer;
import com.astamuse.asta4d.web.dispatch.response.provider.JsonDataProvider;
import com.astamuse.asta4d.web.util.DeclareInstanceUtil;

public class JsonTransformer implements ResultTransformer {

    @Override
    public Object transformToContentProvider(Object result) {
        JsonDataProvider provider = DeclareInstanceUtil.createInstance(JsonDataProvider.class);
        provider.setData(result);
        return provider;
    }
}
