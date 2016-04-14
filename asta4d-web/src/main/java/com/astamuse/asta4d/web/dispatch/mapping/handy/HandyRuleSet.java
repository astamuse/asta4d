package com.astamuse.asta4d.web.dispatch.mapping.handy;

import java.util.ArrayList;
import java.util.List;

import com.astamuse.asta4d.web.dispatch.HttpMethod;
import com.astamuse.asta4d.web.dispatch.HttpMethod.ExtendHttpMethod;
import com.astamuse.asta4d.web.dispatch.mapping.UrlMappingRule;
import com.astamuse.asta4d.web.dispatch.mapping.UrlMappingRuleRewriter;
import com.astamuse.asta4d.web.dispatch.mapping.UrlMappingRuleSet;
import com.astamuse.asta4d.web.dispatch.mapping.UrlMappingRuleSetHelper;
import com.astamuse.asta4d.web.dispatch.mapping.handy.base.HandyRuleConfigurer;
import com.astamuse.asta4d.web.dispatch.mapping.handy.rest.JsonSupportRuleSet;
import com.astamuse.asta4d.web.dispatch.mapping.handy.rest.XmlSupportRuleSet;
import com.astamuse.asta4d.web.dispatch.mapping.handy.template.TemplateRuleHelper;
import com.astamuse.asta4d.web.dispatch.mapping.handy.template.TemplateRuleWithForward;
import com.astamuse.asta4d.web.dispatch.request.ResultTransformer;
import com.astamuse.asta4d.web.dispatch.request.transformer.Asta4DPageTransformer;
import com.astamuse.asta4d.web.dispatch.request.transformer.DefaultExceptionTransformer;
import com.astamuse.asta4d.web.dispatch.request.transformer.DefaultStringTransformer;
import com.astamuse.asta4d.web.dispatch.request.transformer.DefaultTemplateNotFoundExceptionTransformer;
import com.astamuse.asta4d.web.dispatch.request.transformer.SimpleTypeMatchTransformer;

@SuppressWarnings("unchecked")
public class HandyRuleSet<A extends HandyRuleAfterAddSrc<?, ?, ?>, D extends HandyRuleAfterAddSrcAndTarget<?>>
        extends UrlMappingRuleSet<A, D>implements JsonSupportRuleSet, XmlSupportRuleSet, HandyRuleBuilder {

    private static final String DEFAULT_RULE_TYPE = "template";

    protected final static class GlobalForwardHolder {
        Object result;
        String targetPath;
        Integer status;
        boolean isRedirect;

        public GlobalForwardHolder(Object result, String targetPath, Integer status, boolean isRedirect) {
            super();
            this.result = result;
            this.targetPath = targetPath;
            this.status = status;
            this.isRedirect = isRedirect;
        }
    }

    protected List<GlobalForwardHolder> forwardHolderList = new ArrayList<>();

    protected UrlMappingRule createDefaultRule(HttpMethod method, ExtendHttpMethod extendMethod, String sourcePath) {
        UrlMappingRule rule = new UrlMappingRule();
        ruleList.add(rule);
        rule.setMethod(method);
        rule.setExtendMethod(extendMethod);
        rule.setSourcePath(sourcePath);
        rule.setSeq(Sequencer.incrementAndGet());
        rule.setPriority(DEFAULT_PRIORITY);
        rule.setRuleMatcher(defaultRuleMatcher);
        // register the default result transformer reorganizer, can be overridden by other apis
        UrlMappingRuleSetHelper.setRuleType(rule, DEFAULT_RULE_TYPE);
        UrlMappingRuleSetHelper.addBeforeSortRuleRewritter(rule, new UrlMappingRuleRewriter() {
            @Override
            public void rewrite(UrlMappingRule rule) {

                if (DEFAULT_RULE_TYPE.equals(UrlMappingRuleSetHelper.getRuleType(rule))) {
                    // OK
                } else {
                    return;
                }

                List<ResultTransformer> transformerList = rule.getResultTransformerList();
                // find out the default forward rule
                ResultTransformer transformer, defaultTransformer = null;

                int size = transformerList.size();
                for (int i = size - 1; i >= 0; i--) {
                    transformer = transformerList.get(i);
                    if (transformer instanceof SimpleTypeMatchTransformer) {
                        if (((SimpleTypeMatchTransformer) transformer).isAsDefaultMatch()) {
                            defaultTransformer = transformer;
                            transformerList.remove(i);
                            break;
                        }
                    }
                }

                boolean hasHandler = !rule.getHandlerList().isEmpty();

                if (hasHandler) {
                    // add global forward
                    addGlobalForwardTransformers(transformerList);
                    // add String transformers for non default forward rules(forward
                    // by result)
                    transformerList.add(new DefaultStringTransformer());

                    // add global forward again for possible
                    // exceptions on the above transformers
                    addGlobalForwardTransformers(transformerList);
                    // add String transformers for the global forword rules
                    transformerList.add(new DefaultStringTransformer());
                }

                // add default forward rule
                if (defaultTransformer != null) {
                    transformerList.add(defaultTransformer);
                    // add default String transformers for the default forward rule
                    transformerList.add(new DefaultStringTransformer());

                    // add global forward of Throwable result again again (!!!) for
                    // possible exceptions on the above transformers
                    addGlobalForwardTransformers(transformerList);
                    // add String transformers for the global throwable result
                    // forword rules
                    transformerList.add(new DefaultStringTransformer());
                }

                // add the last insured transformers
                transformerList.add(new DefaultTemplateNotFoundExceptionTransformer());
                transformerList.add(new DefaultExceptionTransformer());
                transformerList.add(new Asta4DPageTransformer());
            }
        });

        return rule;
    }

    protected void addGlobalForwardTransformers(List<ResultTransformer> transformerList) {
        for (GlobalForwardHolder forwardHolder : forwardHolderList) {
            if (forwardHolder.isRedirect) {
                transformerList.add(TemplateRuleHelper.redirectTransformer(forwardHolder.result, forwardHolder.targetPath));
            } else if (forwardHolder.status == null) {
                transformerList.add(TemplateRuleHelper.forwardTransformer(forwardHolder.result, forwardHolder.targetPath));
            } else {
                transformerList
                        .add(TemplateRuleHelper.forwardTransformer(forwardHolder.result, forwardHolder.targetPath, forwardHolder.status));
            }
        }
    }

    public void addGlobalForward(Object result, String targetPath, int status) {
        forwardHolderList.add(new GlobalForwardHolder(result, targetPath, status, false));
    }

    public void addGlobalForward(Object result, String targetPath) {
        forwardHolderList.add(new GlobalForwardHolder(result, targetPath, null, false));
    }

    public void addGlobalRedirect(Object result, String targetPath) {
        forwardHolderList.add(new GlobalForwardHolder(result, targetPath, null, true));
    }

    public A add(String sourcePath) {
        return add(defaultMethod, sourcePath);
    }

    public A add(HttpMethod method, String sourcePath) {
        UrlMappingRule rule = createDefaultRule(method, null, sourcePath);
        A handyRule = buildHandyRuleAfterAddSrc(rule);
        return handyRule;
    }

    public A add(ExtendHttpMethod extendMethod, String sourcePath) {
        UrlMappingRule rule = createDefaultRule(HttpMethod.UNKNOWN, extendMethod, sourcePath);
        A handyRule = buildHandyRuleAfterAddSrc(rule);
        return handyRule;
    }

    public D add(String sourcePath, String targetPath) {
        return add(defaultMethod, sourcePath, targetPath);
    }

    public D add(HttpMethod method, String sourcePath, String targetPath) {
        UrlMappingRule rule = createDefaultRule(method, null, sourcePath);
        @SuppressWarnings("rawtypes")
        TemplateRuleWithForward trf = new TemplateRuleWithForward() {
            @Override
            public void configureRule(HandyRuleConfigurer configure) {
                configure.configure(rule);
            }
        };
        trf.forward(targetPath);
        return buildHandyRuleAfterAddSrcAndTarget(rule);
    }

    public D add(ExtendHttpMethod extendMethod, String sourcePath, String targetPath) {
        UrlMappingRule rule = createDefaultRule(HttpMethod.UNKNOWN, extendMethod, sourcePath);
        @SuppressWarnings("rawtypes")
        TemplateRuleWithForward trf = new TemplateRuleWithForward() {
            @Override
            public void configureRule(HandyRuleConfigurer configure) {
                configure.configure(rule);
            }
        };
        trf.forward(targetPath);
        return buildHandyRuleAfterAddSrcAndTarget(rule);
    }
}
