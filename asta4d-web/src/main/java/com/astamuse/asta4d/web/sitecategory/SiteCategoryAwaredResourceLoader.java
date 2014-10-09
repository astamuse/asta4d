package com.astamuse.asta4d.web.sitecategory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.StringUtils;

public abstract class SiteCategoryAwaredResourceLoader<T> {

    private static final String NO_EXISTING_PATH = "##NO_EXISTING_PATH##";

    private Map<String, Map<String, String>> existingPathMap = new ConcurrentHashMap<>();

    public SiteCategoryAwaredResourceLoader() {

    }

    /**
     * 
     * @param path
     * @return null means the target resource is not found
     * @throws Exception
     */
    public abstract T load(String path) throws Exception;

    public T load(String[] categories, String path) throws Exception {
        Map<String, String> pathMap = null;
        String existingPath = null;
        for (String category : categories) {
            pathMap = retrievePathMap(category);
            existingPath = pathMap.get(path);

            if (NO_EXISTING_PATH.equals(existingPath)) {
                continue;
            } else {
                if (existingPath == null) {// not check yet
                    String tryPath = createCategorySpecialPath(category, path);
                    T res = load(tryPath);
                    if (res == null) {
                        pathMap.put(path, NO_EXISTING_PATH);
                        continue;
                    } else {
                        pathMap.put(path, tryPath);
                        return res;
                    }
                } else {
                    return load(existingPath);
                }
            }
        }
        // it also means not found
        return null;
    }

    protected String createCategorySpecialPath(String category, String path) {
        if (StringUtils.isEmpty(category)) {
            return path;
        } else {
            return "/" + category + path;
        }
    }

    protected Map<String, String> retrievePathMap(String category) {
        Map<String, String> pathMap = existingPathMap.get(category);
        if (pathMap == null) {
            pathMap = new ConcurrentHashMap<>();
            existingPathMap.put(category, pathMap);
        }
        return pathMap;
    }

}
