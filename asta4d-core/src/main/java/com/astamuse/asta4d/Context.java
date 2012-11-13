package com.astamuse.asta4d;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;

import org.jsoup.nodes.Element;

import com.astamuse.asta4d.extnode.ExtNodeConstants;

public class Context {

    public final static String SCOPE_GLOBAL = "global";

    public final static String SCOPE_DEFAULT = "default";

    public final static String SCOPE_ATTR = "attr";

    public final static String SCOPE_EXT_ATTR = "ext_attr";

    private final static String KEY_CURRENT_LOCALE = "current_locale";

    private final static String KEY_CURRENT_RENDERING_ELEMENT = "current_rendering_element";

    private final static ThreadLocal<Context> instanceHolder = new ThreadLocal<>();

    private final static Map<String, Object> globalMap = new ConcurrentHashMap<>();

    private Configuration configuration;

    // this map is not thought to be used in multi threads since the instance of
    // Context is thread single.
    private Map<String, Map<String, Object>> scopeMap = new HashMap<>();

    // private List

    public final static Context getCurrentThreadContext() {
        return instanceHolder.get();
    }

    public final static void setCurrentThreadContext(Context context) {
        instanceHolder.set(context);
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }

    public void setCurrentRenderingElement(Element elem) {
        setData(KEY_CURRENT_RENDERING_ELEMENT, elem);
    }

    public Element getCurrentRenderingElement() {
        return getData(KEY_CURRENT_RENDERING_ELEMENT);
    }

    public void setData(String key, Object data) {
        setData(SCOPE_DEFAULT, key, data);
    }

    public void setData(String scope, String key, Object data) {
        Map<String, Object> dataMap = acquireMapForScope(scope);
        if (data == null) {
            dataMap.remove(key);
        } else {
            dataMap.put(key, data);
        }
    }

    public <T> T getData(String key) {
        return getData(SCOPE_DEFAULT, key);
    }

    @SuppressWarnings("unchecked")
    public <T> T getData(String scope, String key) {
        if (scope.equals(SCOPE_ATTR)) {
            return (T) retrieveElementAttr(key);
        } else {
            Map<String, Object> dataMap = acquireMapForScope(scope);
            return (T) dataMap.get(key);
        }
    }

    public Locale getCurrentLocale() {
        Locale currentLocale = getData(KEY_CURRENT_LOCALE);
        if (currentLocale != null) {
            return currentLocale;
        }
        return Locale.getDefault();
    }

    public void setCurrentLocale(Locale locale) {
        setData(KEY_CURRENT_LOCALE, locale);
    }

    private Object retrieveElementAttr(String key) {
        String dataRef = ExtNodeConstants.ATTR_DATAREF_PREFIX_WITH_NS + key;
        Element elem = getCurrentRenderingElement();
        Object value = null;
        while (value == null && elem != null) {
            // for a faked snippet node, we will just jump over it
            if (elem.tagName().equals(ExtNodeConstants.SNIPPET_NODE_TAG)) {
                String type = elem.attr(ExtNodeConstants.SNIPPET_NODE_ATTR_TYPE);
                if (type.equals(ExtNodeConstants.SNIPPET_NODE_ATTR_TYPE_FAKE)) {
                    elem = elem.parent();
                    continue;
                }
            }
            if (elem.hasAttr(dataRef)) {
                String id = elem.attr(dataRef);
                value = getData(SCOPE_EXT_ATTR, id);
            } else if (elem.hasAttr(key)) {
                value = elem.attr(key);
            }
            elem = elem.parent();
        }
        return value;
    }

    protected final Map<String, Object> acquireMapForScope(String scope) {
        Map<String, Object> dataMap = scopeMap.get(scope);
        if (dataMap == null) {
            dataMap = createMapForScope(scope);
            if (dataMap == null) {
                dataMap = new HashMap<>();
            }
            scopeMap.put(scope, dataMap);
        }
        return dataMap;
    }

    /**
     * sub class can override this method for custom scope map instance
     * 
     * @param scope
     * @return
     */
    protected Map<String, Object> createMapForScope(String scope) {
        Map<String, Object> map = null;
        switch (scope) {
        case SCOPE_GLOBAL:
            map = globalMap;
            break;
        default:
            map = new HashMap<>();
        }
        return map;
    }

    public void clearSavedData() {
        scopeMap.clear();
    }

    public Context clone() {
        Context newCtx = new Context();
        newCtx.configuration = configuration;
        copyScopesTo(newCtx);
        return newCtx;
    }

    protected void copyScopesTo(Context newCtx) {
        for (String scope : scopeMap.keySet()) {
            newCtx.scopeMap.put(scope, getScopeDataMapCopy(scope));
        }
    }

    protected Map<String, Object> getScopeDataMapCopy(String scope) {
        Map<String, Object> map = acquireMapForScope(scope);
        return new HashMap<>(map);
    }

    public final static void with(Context context, Runnable runner) {
        Context oldContext = Context.getCurrentThreadContext();
        try {
            Context.setCurrentThreadContext(context);
            runner.run();
        } finally {
            Context.setCurrentThreadContext(oldContext);
        }
    }

    public final static <T> T with(Context context, Callable<T> caller) throws Exception {
        Context oldContext = Context.getCurrentThreadContext();
        try {
            Context.setCurrentThreadContext(context);
            return caller.call();
        } finally {
            Context.setCurrentThreadContext(oldContext);
        }
    }

}
