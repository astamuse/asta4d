package com.astamuse.asta4d.template;

import java.util.ArrayList;
import java.util.List;

public abstract class MultiSearchPathResourceLoader<T> {

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
        T result = loadResource(searchName);
        if (result == null) {
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
