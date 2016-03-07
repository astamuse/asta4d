package com.astamuse.asta4d.web.dispatch.mapping.handy.template;

import com.astamuse.asta4d.web.dispatch.request.MultiResultHolder;
import com.astamuse.asta4d.web.dispatch.request.ResultTransformer;
import com.astamuse.asta4d.web.dispatch.request.transformer.SimpleTypeMatchTransformer;
import com.astamuse.asta4d.web.dispatch.response.provider.HeaderInfoProvider;

public class TemplateRuleHelper {

    public static ResultTransformer redirectTransformer(Object result, String targetUrl) {
        return new SimpleTypeMatchTransformer(result, "redirect:" + targetUrl);
    }

    public static ResultTransformer forwardTransformer(Object result, String target) {
        return new SimpleTypeMatchTransformer(result, target);
    }

    public static ResultTransformer forwardTransformer(Object result, String targetPath, int status) {
        MultiResultHolder mrh = new MultiResultHolder();
        mrh.addResult(new HeaderInfoProvider(status));
        mrh.addResult(targetPath);
        return new SimpleTypeMatchTransformer(result, mrh);
    }

}
