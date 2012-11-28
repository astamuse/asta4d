package com.astamuse.asta4d.web.dispatch.request.transformer;

import com.astamuse.asta4d.web.dispatch.request.ResultTransformer;
import com.astamuse.asta4d.web.dispatch.response.provider.RedirectTargetProvider;
import com.astamuse.asta4d.web.util.DeclareInstanceUtil;

public class String2RedirctTransformer implements ResultTransformer {

    @Override
    public Object transformToContentProvider(Object result) {
        if (result instanceof String) {
            String target = result.toString();
            if (target.startsWith("redirect:")) {
                String path = target.substring("redirect:".length());
                RedirectTargetProvider provider = DeclareInstanceUtil.createInstance(RedirectTargetProvider.class);
                provider.setTargetPath(path);
                return provider;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

}
