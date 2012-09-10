package org.jsoupit;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.jsoup.nodes.Element;
import org.jsoupit.template.extnode.ExtNodeConstants;

public class Context {

    public final static String SCOPE_GLOBAL = "global";

    public final static String SCOPE_DEFAULT = "default";

    public final static String SCOPE_ATTR = "attr";

    private final static ThreadLocal<Context> instanceHolder = new ThreadLocal<>();

    private final static Map<String, Object> globalMap = new ConcurrentHashMap<>();

    private Element currentRenderingElement = null;

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
        currentRenderingElement = elem;
    }

    public Element getCurrentRenderingElement() {
        return currentRenderingElement;
    }

    public void setData(String key, Object data) {
        setData(SCOPE_DEFAULT, key, data);
    }

    public void setData(String scope, String key, Object data) {
        Map<String, Object> dataMap = getMapForScope(scope);
        dataMap.put(key, data);
    }

    public <T> T getData(String key) {
        return getData(SCOPE_DEFAULT, key);
    }

    @SuppressWarnings("unchecked")
    public <T> T getData(String scope, String key) {
        if (scope.equals(SCOPE_ATTR)) {
            return (T) retrieveElementAttr(key);
        } else {
            Map<String, Object> dataMap = getMapForScope(scope);
            return (T) dataMap.get(key);
        }
    }

    private String retrieveElementAttr(String key) {
        Element elem = currentRenderingElement;
        String value = null;
        while (value == null && elem != null) {
            value = findAttr(elem, key);
            elem = elem.parent();
        }
        return value;
    }

    private String findAttr(Element elem, String attr) {
        String value = null;
        if (elem.tagName().equals(ExtNodeConstants.SNIPPET_NODE_TAG)) {
            String type = elem.attr(ExtNodeConstants.SNIPPET_NODE_ATTR_TYPE);
            if (type.equals(ExtNodeConstants.SNIPPET_NODE_ATTR_TYPE_FAKE)) {
                // for a faked snippet node, we will try to retrieve the
                // attribute values from its directive single child node
                Element realTarget = elem.children().first();
                // It is possible that this real target is still a faked
                // snippet, so we recursive call findAttr
                value = findAttr(realTarget, attr);
            } else if (elem.hasAttr(attr)) {
                value = elem.attr(attr);
            }
        } else if (elem.hasAttr(attr)) {
            value = elem.attr(attr);
        }
        return value;
    }

    protected final Map<String, Object> getMapForScope(String scope) {
        Map<String, Object> dataMap = scopeMap.get(scope);
        if (dataMap == null) {
            dataMap = acquireMapForScope(scope);
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
    protected Map<String, Object> acquireMapForScope(String scope) {
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

}
