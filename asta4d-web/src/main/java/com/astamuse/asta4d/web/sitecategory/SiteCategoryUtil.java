package com.astamuse.asta4d.web.sitecategory;

import com.astamuse.asta4d.web.WebApplicationContext;

public class SiteCategoryUtil {

    private static final String CategoryKey = SiteCategoryUtil.class.getName() + "#CategoryKey";

    public static final void setCurrentRequestSearchCategories(String... categories) {
        WebApplicationContext.getCurrentThreadContext().setData(CategoryKey, categories);
    }

    public static final String[] getCurrentRequestSearchCategories() {
        return WebApplicationContext.getCurrentThreadContext().getData(CategoryKey);
    }
}
