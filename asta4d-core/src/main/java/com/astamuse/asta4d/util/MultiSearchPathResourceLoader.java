package com.astamuse.asta4d.util;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class MultiSearchPathResourceLoader<T> {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private List<String> searchPathList = new ArrayList<>();

    public MultiSearchPathResourceLoader() {
    }

    public T searchResource(String pathSeparator, String... names) {
        if (names == null || names.length < 1) {
            throw new IllegalArgumentException("must specify one or more names");
        }
        return searchResource(pathSeparator, -1, names);
    }

    private T searchResource(String pathSeparator, int index, String... names) {
        List<String> searchNames = new ArrayList<>();
        for (String name : names) {
            String searchName;
            if (index < 0) {
                searchName = name;
            } else if (index >= searchPathList.size()) {
                logger.debug("Did not find any associated resource for name {}", name);
                return null;
            } else {
                String searchPath = searchPathList.get(index);
                boolean pathWithSeparator = searchPath.endsWith(pathSeparator);
                boolean nameWithSeparator = name.startsWith(pathSeparator);
                if (pathWithSeparator && nameWithSeparator) {
                    searchName = searchPath + name.substring(1);
                } else if (pathWithSeparator || nameWithSeparator) {
                    searchName = searchPath + name;
                } else { // nether has
                    searchName = searchPath + pathSeparator + name;
                }
            }
            logger.debug("try load resource for {}", searchName);
            T result = loadResource(searchName);
            if (result != null) {
                return result;
            }
            searchNames.add(searchName);
        }
        logger.debug("load resource for {} failed", searchNames.toString());
        return searchResource(pathSeparator, index + 1, names);
    }

    protected abstract T loadResource(String name);

    public List<String> getSearchPathList() {
        return new ArrayList<>(searchPathList);
    }

    public void setSearchPathList(List<String> searchPathList) {
        this.searchPathList.clear();
        if (searchPathList != null) {
            this.searchPathList.addAll(searchPathList);
        }
    }

}
