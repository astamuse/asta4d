package org.jsoupit.template.snippet.resolve;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jsoupit.Context;
import org.jsoupit.template.snippet.SnippetNotResovlableException;

public class DefaultSnippetResolver implements SnippetResolver {

    private List<String> searchPathList = new ArrayList<>();

    private final static String MapCacheKey = DefaultSnippetResolver.class.getName() + "##MapCacheKey";

    @Override
    public Object findSnippet(String snippetName) throws SnippetNotResovlableException {
        Object instance = getSnippetInstance(snippetName);
        if (instance == null) {
            instance = retrieveInstance(snippetName);
            setSnippetInstance(snippetName, instance);
        }
        return instance;
    }

    protected Object retrieveInstance(String snippetName) throws SnippetNotResovlableException {
        // TODO support base package
        try {
            Class<?> cls = findClass(snippetName, -1);
            return cls.newInstance();
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            throw new SnippetNotResovlableException(String.format("Snippet [%s] resolve failed.", snippetName), e);
        }
    }

    private Class<?> findClass(String snippetName, int searchIndex) throws ClassNotFoundException {
        String searchName;
        if (searchIndex < 0) {
            searchName = snippetName;
        } else if (searchIndex >= searchPathList.size()) {
            throw new ClassNotFoundException("Can not found class for snippet name:" + snippetName);
        } else {
            String path = searchPathList.get(searchIndex);
            searchName = path;
            if (!path.endsWith(".")) {
                searchName += ".";
            }
            searchName += snippetName;
        }
        try {
            Class<?> clz = Class.forName(searchName);
            return clz;
        } catch (ClassNotFoundException e) {
            return findClass(snippetName, searchIndex + 1);
        }

    }

    public List<String> getSearchPathList() {
        return new ArrayList<>(searchPathList);
    }

    public void setSearchPathList(List<String> searchPathList) {
        this.searchPathList.clear();
        if (searchPathList != null) {
            this.searchPathList.addAll(searchPathList);
        }
    }

    // TODO maybe we do not need to create a recursive map
    private Map<String, Object> getCacheMap() {
        Context context = Context.getCurrentThreadContext();
        Map<String, Object> map = context.getData(MapCacheKey);
        if (map == null) {
            map = new HashMap<>();
            context.setData(MapCacheKey, map);
        }
        return map;
    }

    public Object getSnippetInstance(String snippetName) {
        return getCacheMap().get(snippetName);
    }

    public void setSnippetInstance(String snippetName, Object snippet) {
        getCacheMap().put(snippetName, snippet);
    }

}
