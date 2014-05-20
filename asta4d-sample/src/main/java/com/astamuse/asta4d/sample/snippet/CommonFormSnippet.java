package com.astamuse.asta4d.sample.snippet;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import com.astamuse.asta4d.web.form.IntelligentFormSnippet;

public class CommonFormSnippet extends IntelligentFormSnippet {

    private static Set<String> NonEditSteps = new HashSet<>();
    static {
        NonEditSteps.add("confirm");
        NonEditSteps.add("complete");
    }

    @Override
    protected boolean renderForEdit(String step) {
        if (StringUtils.isEmpty(step)) {
            return true;
        } else {
            return !NonEditSteps.contains(step.toLowerCase());
        }
    }
}
