package org.jsoupit.misc.spring.mvc.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.jsoupit.misc.spring.mvc.controller.annotation.SubController;
import org.springframework.web.bind.annotation.RequestMethod;

public abstract class UrlMappingRuleHolder {

    private static class DefaultController {

        private String targetPath;

        public DefaultController(String targetPath) {
            this.targetPath = targetPath;
        }

        @SubController
        public String process() {
            return targetPath;
        }
    };

    private List<MappingRule> urlRules = new ArrayList<>();

    private int seq = 0;

    private final static int DEFAULT_PRIORITY = 1000;

    private volatile List<MappingRule> sortedRuleList = null;

    protected List<MappingRule> getSortedRuleList() {
        if (sortedRuleList != null) {
            return sortedRuleList;
        }
        // TODO it seem that there is a DCL problem
        // Maybe we do not need to set the sortedRuleList as volatile
        // but I am not sure that. Anyway, I do not want a lock on the field
        synchronized (this) {
            sortedRuleList = new ArrayList<>(urlRules);
            Collections.sort(sortedRuleList, new Comparator<MappingRule>() {
                @Override
                public int compare(MappingRule r1, MappingRule r2) {
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
    }

    protected void addRule(RequestMethod method, String sourceUrl, int priority, Object pathvarRewritter, Object subController) {
        urlRules.add(new MappingRule(seq++, method, sourceUrl, priority, pathvarRewritter, subController));
    }

    // default to get
    protected void addRule(String sourceUrl, String targetPath, int priority, Object pathvarRewritter) {
        addRule(RequestMethod.GET, sourceUrl, priority, pathvarRewritter, new DefaultController(targetPath));
    }

    protected void addRule(String sourceUrl, String targetPath, Object pathvarRewritter) {
        addRule(sourceUrl, targetPath, DEFAULT_PRIORITY, pathvarRewritter);
    }

    protected void addRule(String sourceUrl, String targetPath) {
        addRule(sourceUrl, targetPath, null);
    }

    // for other method such as post
    protected void addRule(RequestMethod method, String sourceUrl, Object subController) {
        addRule(method, sourceUrl, DEFAULT_PRIORITY, null, subController);
    }
}
