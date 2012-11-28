package com.astamuse.asta4d.web.dispatch.request.transformer;

import com.astamuse.asta4d.web.dispatch.request.ResultTransformer;
import com.astamuse.asta4d.web.dispatch.response.provider.HeaderInfo;
import com.astamuse.asta4d.web.dispatch.response.provider.HeaderInfoProvider;
import com.astamuse.asta4d.web.util.DeclareInstanceUtil;

public class HeaderInfoTransformer implements ResultTransformer {

    @Override
    public Object transformToContentProvider(Object result) {
        if (result instanceof HeaderInfo) {
            HeaderInfoProvider provider = DeclareInstanceUtil.createInstance(HeaderInfoProvider.class);
            provider.setInfo((HeaderInfo) result);
            return provider;
        } else {
            return null;
        }
    }

}
