package com.astamuse.asta4d.template.snippet.resolve;

import java.util.HashMap;
import java.util.Map;

import com.astamuse.asta4d.Context;
import com.astamuse.asta4d.template.MultiSearchPathResourceLoader;
import com.astamuse.asta4d.template.snippet.SnippetNotResovlableException;

public class DefaultSnippetResolver extends MultiSearchPathResourceLoader<Class<?>> implements SnippetResolver {

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
            Class<?> cls = searchResource(snippetName, ".");
            if (cls == null) {
                throw new ClassNotFoundException("Can not found class for snippet name:" + snippetName);
            }
            return cls.newInstance();
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            throw new SnippetNotResovlableException(String.format("Snippet [%s] resolve failed.", snippetName), e);
        }
    }

    @Override
    protected Class<?> loadResource(String name) {
        try {
            Class<?> clz = Class.forName(name);
            return clz;
        } catch (ClassNotFoundException e) {
            return null;
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
