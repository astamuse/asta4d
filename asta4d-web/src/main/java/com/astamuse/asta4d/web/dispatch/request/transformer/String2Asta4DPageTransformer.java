package com.astamuse.asta4d.web.dispatch.request.transformer;

import com.astamuse.asta4d.web.dispatch.request.ResultTransformer;
import com.astamuse.asta4d.web.dispatch.response.provider.Asta4DPageProvider;
import com.astamuse.asta4d.web.util.DeclareInstanceUtil;

public class String2Asta4DPageTransformer implements ResultTransformer {

    @Override
    public Object transformToContentProvider(Object result) {
        if (result instanceof String) {
            Asta4DPageProvider provider = DeclareInstanceUtil.createInstance(Asta4DPageProvider.class);
            provider.setPath(result.toString());
            return provider;
        } else {
            return null;
        }
    }

}
