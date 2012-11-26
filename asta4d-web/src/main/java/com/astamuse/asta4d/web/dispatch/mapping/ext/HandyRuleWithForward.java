package com.astamuse.asta4d.web.dispatch.mapping.ext;

import com.astamuse.asta4d.web.dispatch.mapping.ResultDescriptor;
import com.astamuse.asta4d.web.dispatch.mapping.UrlMappingRule;
import com.astamuse.asta4d.web.dispatch.response.Asta4DPageWriter;
import com.astamuse.asta4d.web.dispatch.response.ContentWriter;
import com.astamuse.asta4d.web.dispatch.response.HeaderWriter;
import com.astamuse.asta4d.web.dispatch.response.JsonWriter;
import com.astamuse.asta4d.web.dispatch.response.RedirectActionWriter;
import com.astamuse.asta4d.web.dispatch.response.SerialWriter;
import com.astamuse.asta4d.web.dispatch.response.provider.Asta4DPageProvider;
import com.astamuse.asta4d.web.dispatch.response.provider.HeaderInfoHoldingContent;
import com.astamuse.asta4d.web.dispatch.response.provider.RedirectTargetProvider;
import com.astamuse.asta4d.web.dispatch.response.provider.RestResultProvider;
import com.astamuse.asta4d.web.util.DeclareInstanceUtil;

public class HandyRuleWithForward {

    protected UrlMappingRule rule;

    public HandyRuleWithForward(UrlMappingRule rule) {
        this.rule = rule;
    }

    public HandyRuleWithForward forward(Object result, Object contentProvider, ContentWriter writer) {
        Object cpInstance = DeclareInstanceUtil.createInstance(contentProvider);
        rule.getContentProviderMap().add(new ResultDescriptor(result, cpInstance, writer));
        return this;
    }

    public HandyRuleWithForward forward(Object contentProvider, ContentWriter writer) {
        return this.forward(null, contentProvider, writer);
    }

    public HandyRuleWithForward forward(Object result, String targetPath) {
        return this.forward(result, new Asta4DPageProvider(targetPath), new Asta4DPageWriter());
    }

    public HandyRuleWithForward forward(Object result, String targetPath, int status) {
        ContentWriter cw = new SerialWriter(new HeaderWriter(new HeaderInfoHoldingContent(status, null)), new Asta4DPageWriter());
        return this.forward(result, new Asta4DPageProvider(targetPath), cw);
    }

    public HandyRuleWithForward forward(String targetPath) {
        return this.forward(null, targetPath);
    }

    public HandyRuleWithForward forward(String targetPath, int status) {
        return this.forward(null, targetPath, status);
    }

    public HandyRuleWithForward redirect(Object result, String targetPath) {
        return this.forward(result, new RedirectTargetProvider(targetPath), new RedirectActionWriter());
    }

    public HandyRuleWithForward redirect(String targetPath) {
        return this.redirect(null, targetPath);
    }

    public void json(Object contentProvider) {
        this.forward(contentProvider, new JsonWriter());
    }

    public void rest() {
        this.forward(new RestResultProvider(), new HeaderWriter());
    }
}
