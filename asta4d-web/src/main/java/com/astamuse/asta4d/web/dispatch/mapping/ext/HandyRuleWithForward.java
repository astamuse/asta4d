package com.astamuse.asta4d.web.dispatch.mapping.ext;

import com.astamuse.asta4d.web.dispatch.mapping.UrlMappingRule;
import com.astamuse.asta4d.web.dispatch.request.transformer.HeaderInfoTransformer;
import com.astamuse.asta4d.web.dispatch.request.transformer.JsonTransformer;
import com.astamuse.asta4d.web.dispatch.request.transformer.SimpleTypeMatchTransformer;
import com.astamuse.asta4d.web.dispatch.request.transformer.String2Asta4DPageTransformer;
import com.astamuse.asta4d.web.dispatch.request.transformer.String2RedirctTransformer;
import com.astamuse.asta4d.web.dispatch.response.provider.Asta4DPageProvider;
import com.astamuse.asta4d.web.dispatch.response.provider.HeaderInfo;
import com.astamuse.asta4d.web.dispatch.response.provider.HeaderInfoProvider;
import com.astamuse.asta4d.web.dispatch.response.provider.SerialProvider;

public class HandyRuleWithForward {

    private final static String2Asta4DPageTransformer asta4dPageTransformer = new String2Asta4DPageTransformer();

    private final static String2RedirctTransformer redirectTransformer = new String2RedirctTransformer();

    private final static HeaderInfoTransformer headerTransformer = new HeaderInfoTransformer();

    private final static JsonTransformer jsonTransformer = new JsonTransformer();

    protected UrlMappingRule rule;

    public HandyRuleWithForward(UrlMappingRule rule) {
        this.rule = rule;
    }

    private HandyRuleWithForward forward(Object result, Object target) {
        rule.getResultTransformerList().add(new SimpleTypeMatchTransformer(result, target));
        return this;
    }

    public HandyRuleWithForward forward(Object result, String targetPath) {
        return this.forward(result, asta4dPageTransformer.transformToContentProvider(targetPath));
    }

    public HandyRuleWithForward forward(Object result, String targetPath, int status) {
        HeaderInfoProvider header = (HeaderInfoProvider) headerTransformer.transformToContentProvider(new HeaderInfo(status));
        Asta4DPageProvider page = (Asta4DPageProvider) asta4dPageTransformer.transformToContentProvider(targetPath);
        SerialProvider sp = new SerialProvider(header, page);
        return this.forward(result, sp);
    }

    public HandyRuleWithForward redirect(Object result, String targetPath) {
        return this.forward(result, redirectTransformer.transformToContentProvider("redirect:" + targetPath));
    }

    public void forward(String targetPath) {
        this.forward(null, targetPath);
    }

    public void forward(String targetPath, int status) {
        this.forward(null, targetPath, status);
    }

    public void redirect(String targetPath) {
        this.redirect(null, targetPath);
    }

    public void json() {
        rule.getResultTransformerList().add(jsonTransformer);
    }

    public void rest() {
        rule.getResultTransformerList().add(headerTransformer);
    }
}
