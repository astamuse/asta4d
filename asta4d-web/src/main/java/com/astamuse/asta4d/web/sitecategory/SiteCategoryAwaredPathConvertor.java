package com.astamuse.asta4d.web.sitecategory;

public interface SiteCategoryAwaredPathConvertor {
    default String convertCategorySpecifiedPath(String category, String path) {
        if (category.isEmpty()) {// category would not be null
            return path;
        } else {
            if (path.startsWith("/")) {
                return "/" + category + path;
            } else {
                return "/" + category + "/" + path;
            }
        }
    }

}
