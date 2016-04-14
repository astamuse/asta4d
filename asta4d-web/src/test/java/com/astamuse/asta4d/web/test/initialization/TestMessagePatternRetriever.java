package com.astamuse.asta4d.web.test.initialization;

import com.astamuse.asta4d.util.i18n.pattern.JDKResourceBundleMessagePatternRetriever;

public class TestMessagePatternRetriever extends JDKResourceBundleMessagePatternRetriever {
    public String[] getResourceNames() {
        return resourceNames;
    }
}