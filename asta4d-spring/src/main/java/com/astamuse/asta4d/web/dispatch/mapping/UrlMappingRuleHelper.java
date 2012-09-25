package com.astamuse.asta4d.web.dispatch.mapping;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.web.bind.annotation.RequestMethod;

import com.astamuse.asta4d.web.dispatch.annotation.RequestHandler;

public class UrlMappingRuleHelper {

    public static class DefaultHandler {

        private String targetPath;

        public DefaultHandler(String targetPath) {
            this.targetPath = targetPath;
        }

        @RequestHandler
        public String process() {
            return targetPath;
        }
    };

    private List<UrlMappingRule> urlRules = new ArrayList<>();

    private AtomicInteger seq = new AtomicInteger();

    public final static int DEFAULT_PRIORITY = 1000;

    public List<UrlMappingRule> getSortedRuleList() {
        List<UrlMappingRule> sortedRuleList = new ArrayList<>(urlRules);
        Collections.sort(sortedRuleList, new Comparator<UrlMappingRule>() {
            @Override
            public int compare(UrlMappingRule r1, UrlMappingRule r2) {
                int pc = r1.getPriority() - r2.getPriority();
                if (pc != 0) {
                    return pc;
                } else {
                    return r1.getSeq() - r2.getSeq();
                }

            }

        });
        return sortedRuleList;
    }

    public void add(RequestMethod method, String sourceUrl, int priority, Object pathvarRewritter, Object hanlder) {
        urlRules.add(new UrlMappingRule(seq.incrementAndGet(), method, sourceUrl, priority, pathvarRewritter, hanlder));
    }

    // default to get
    public void add(String sourceUrl, String targetPath, int priority, Object pathvarRewritter) {
        add(RequestMethod.GET, sourceUrl, priority, pathvarRewritter, new DefaultHandler(targetPath));
    }

    public void add(String sourceUrl, String targetPath, Object pathvarRewritter) {
        add(sourceUrl, targetPath, DEFAULT_PRIORITY, pathvarRewritter);
    }

    public void add(String sourceUrl, String targetPath) {
        add(sourceUrl, targetPath, null);
    }

    // for other method such as post
    public void add(RequestMethod method, String sourceUrl, Object hanlder) {
        add(method, sourceUrl, DEFAULT_PRIORITY, null, hanlder);
    }
}
