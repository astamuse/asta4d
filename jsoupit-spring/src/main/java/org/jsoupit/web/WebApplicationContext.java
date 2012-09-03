package org.jsoupit.web;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.jsoupit.template.Context;

public class WebApplicationContext extends Context {

    public final static String SCOPE_REQUEST = "request";

    public final static String SCOPE_PATHVAR = "pathvar";

    public final static String SCOPE_QUERYPARAM = "query";

    public final static String SCOPE_QUERYPARAM_ALIAS = "param";

    public final static String SCOPE_SESSION = "session";

    public final static String SCOPE_GLOBAL = "global";

    private final static String SAVEKEY_REQUEST = WebApplicationContext.class.getName() + "##SAVEKEY-REQUEST";

    private final static String SAVEKEY_RESPONSE = WebApplicationContext.class.getName() + "##SAVEKEY-RESPONSE";

    private final static String SAVEKEY_DATAMAP = WebApplicationContext.class.getName() + "##SAVEKEY-DATAMAP-";

    private final static ConcurrentHashMap<String, Object> GlobalDataMap = new ConcurrentHashMap<>();

    public HttpServletRequest getRequest() {
        return this.getData(SAVEKEY_REQUEST);
    }

    public void setRequest(HttpServletRequest request) {
        this.setData(SAVEKEY_REQUEST, request);
    }

    public HttpServletResponse getResponse() {
        return this.getData(SAVEKEY_RESPONSE);
    }

    public void setResponse(HttpServletResponse response) {
        this.setData(SAVEKEY_RESPONSE, response);
    }

    private Map<String, Object> getDataMapFromContext(String scope) {
        String storeKey = SAVEKEY_DATAMAP + scope;
        Map<String, Object> map = getData(storeKey);
        if (map == null) {
            map = new HashMap<>();
            setData(storeKey, map);
        }
        return map;
    }

    public void setData(String scope, String key, Object obj) {
        switch (scope) {
        case SCOPE_REQUEST:
            setData(key, obj);
            break;
        case SCOPE_PATHVAR:
            getDataMapFromContext(SCOPE_PATHVAR).put(key, obj);
            break;
        case SCOPE_QUERYPARAM_ALIAS:
        case SCOPE_QUERYPARAM:
            getDataMapFromContext(SCOPE_QUERYPARAM).put(key, obj);
            break;
        case SCOPE_SESSION:
            HttpSession session = getRequest().getSession(true);
            session.setAttribute(key, obj);
            break;
        case SCOPE_GLOBAL:
            GlobalDataMap.put(key, obj);
            break;
        default:
            setData(key, obj);
        }
    }

    public Object getData(String scope, String key) {
        Object data = null;
        switch (scope) {
        case SCOPE_REQUEST:
            data = getData(key);
            break;
        case SCOPE_PATHVAR:
            data = getDataMapFromContext(SCOPE_PATHVAR).get(key);
            break;
        case SCOPE_QUERYPARAM_ALIAS:
        case SCOPE_QUERYPARAM:
            data = getDataMapFromContext(SCOPE_QUERYPARAM).get(key);
            break;
        case SCOPE_SESSION:
            HttpSession session = getRequest().getSession(true);
            data = session.getAttribute(key);
            break;
        case SCOPE_GLOBAL:
            data = GlobalDataMap.get(key);
            break;
        default:
            data = getData(key);
        }
        return data;
    }

}
