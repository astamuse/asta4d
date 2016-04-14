package com.astamuse.asta4d.web.dispatch.mapping.handy.template;

import com.astamuse.asta4d.web.dispatch.mapping.handy.base.HandyRuleConfigurable;

public interface TemplateRuleWithForward<T extends TemplateRuleWithForward<?>> extends HandyRuleConfigurable {

    @SuppressWarnings("unchecked")
    default T forward(Object result, String targetPath) {
        configureRule(rule -> {
            rule.getResultTransformerList().add(TemplateRuleHelper.forwardTransformer(result, targetPath));
        });
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    default T forward(Object result, String targetPath, int status) {
        configureRule(rule -> {
            rule.getResultTransformerList().add(TemplateRuleHelper.forwardTransformer(result, targetPath, status));
        });
        return (T) this;
    }

    default void forward(String targetPath) {
        this.forward(null, targetPath);
    }

    default void forward(String targetPath, int status) {
        this.forward(null, targetPath, status);
    }

    default void redirect(String targetUrl) {
        this.redirect(null, targetUrl);
    }

    @SuppressWarnings("unchecked")
    default T redirect(Object result, String targetUrl) {
        configureRule(rule -> {
            rule.getResultTransformerList().add(TemplateRuleHelper.redirectTransformer(result, targetUrl));
        });
        return (T) this;
    }
}
