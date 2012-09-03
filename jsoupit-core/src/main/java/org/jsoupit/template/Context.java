package org.jsoupit.template;

import java.util.HashMap;
import java.util.Map;

public class Context {

    private final static ThreadLocal<Context> instanceHolder = new ThreadLocal<>();

    private Configuration configuration;

    // this map is not thought to be used in multi threads since the instance of
    // Context is thread single.
    private Map<String, Object> dataMap = new HashMap<>();

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

    /*
     * private String buildKeyForClass(Class<?> cls) { // magic numbers to avoid
     * conflicts return "#14345345#-class-" + cls.getName() + "#234354352#"; }
     * 
     * public void setData(Class<?> cls, Object data) {
     * setData(buildKeyForClass(cls), data); }
     * 
     * public <T> T getData(Class<T> cls) { return
     * getData(buildKeyForClass(cls)); }
     */

    public void setData(String key, Object data) {
        dataMap.put(key, data);
    }

    @SuppressWarnings("unchecked")
    public <T> T getData(String key) {
        return (T) dataMap.get(key);
    }

    public void clearSavedData() {
        dataMap.clear();
    }

}
