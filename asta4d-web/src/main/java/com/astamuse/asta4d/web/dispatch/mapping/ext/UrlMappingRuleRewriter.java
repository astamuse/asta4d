package com.astamuse.asta4d.web.dispatch.mapping.ext;

import com.astamuse.asta4d.web.dispatch.mapping.UrlMappingRule;

public interface UrlMappingRuleRewriter {
    public void rewrite(UrlMappingRule rule);
}
