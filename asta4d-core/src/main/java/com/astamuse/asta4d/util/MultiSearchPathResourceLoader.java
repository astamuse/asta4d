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

    public T searchResource(String name, String pathSeparator) {
        return searchResource(name, pathSeparator, -1);
    }

    private T searchResource(String name, String pathSeparator, int index) {
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
        if (result == null) {
            logger.debug("load resource for {} failed", searchName);
            return searchResource(name, pathSeparator, index + 1);
        } else {
            return result;
        }
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
