package com.astamuse.asta4d.web;

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.astamuse.asta4d.Context;

public class WebApplicationContext extends Context {

    public final static String SCOPE_REQUEST = "request";

    public final static String SCOPE_PATHVAR = "pathvar";

    public final static String SCOPE_QUERYPARAM = "query";

    public final static String SCOPE_QUERYPARAM_ALIAS = "param";

    public final static String SCOPE_SESSION = "session";

    public final static String SCOPE_HEADER = "header";

    public final static String SCOPE_COOKIE = "cookie";

    public final static String SCOPE_FLASH = "flash";

    private final static String SAVEKEY_REQUEST = WebApplicationContext.class.getName() + "##SAVEKEY-REQUEST";

    private final static String SAVEKEY_RESPONSE = WebApplicationContext.class.getName() + "##SAVEKEY-RESPONSE";

    private final static String SESSIONKEY_DATAMAP = WebApplicationContext.class.getName() + "##SESSIONKEY_DATAMAP";

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

    @SuppressWarnings("unchecked")
    @Override
    protected Map<String, Object> createMapForScope(String scope) {
        Map<String, Object> map = null;
        switch (scope) {
        case SCOPE_SESSION:
            HttpSession session = getRequest().getSession(true);
            Map<String, Object> dataMap = (Map<String, Object>) session.getAttribute(SESSIONKEY_DATAMAP);
            if (dataMap == null) {
                // TODO I think there would be a visibility problem across
                // threads, so we must fix it at sometime.
                synchronized (SESSIONKEY_DATAMAP) {
                    dataMap = new ConcurrentHashMap<>();
                    session.setAttribute(SESSIONKEY_DATAMAP, dataMap);
                }
            }
            map = dataMap;
            break;
        case SCOPE_HEADER: {
            map = new HashMap<>();
            HttpServletRequest request = getRequest();
            Enumeration<String> headerNames = request.getHeaderNames();
            String name;
            String value;
            while (headerNames.hasMoreElements()) {
                name = headerNames.nextElement();
                // TODO we should consider the situation of multi header values
                value = request.getHeader(name);
                map.put(name, value);
            }
            map = Collections.unmodifiableMap(map);
        }
            break;
        case SCOPE_COOKIE: {
            map = new HashMap<>();
            HttpServletRequest request = getRequest();
            Cookie[] cookies = request.getCookies();
            for (Cookie cookie : cookies) {
                map.put(cookie.getName(), cookie);
            }
            map = Collections.unmodifiableMap(map);
        }
            break;
        case SCOPE_QUERYPARAM_ALIAS:
            map = super.acquireMapForScope(SCOPE_QUERYPARAM);
            break;
        case SCOPE_REQUEST:
            map = super.acquireMapForScope(SCOPE_DEFAULT);
            break;
        case SCOPE_QUERYPARAM:
            map = new HashMap<>();
            HttpServletRequest request = getRequest();
            Enumeration<String> paramNames = request.getParameterNames();
            while (paramNames.hasMoreElements()) {
                String name = paramNames.nextElement();
                // TODO we should consider the situation of multi parameter
                // values
                String value = request.getParameter(name);
                map.put(name, value);
            }
            map = Collections.unmodifiableMap(map);
            break;
        case SCOPE_PATHVAR:
        default:
            map = super.createMapForScope(scope);
        }
        return map;
    }

    public Context clone() {
        Context newCtx = new WebApplicationContext();
        newCtx.setConfiguration(this.getConfiguration());
        copyScopesTo(newCtx);
        return newCtx;
    }

    protected Map<String, Object> getScopeDataMapCopy(String scope) {
        Map<String, Object> map;
        switch (scope) {
        case SCOPE_HEADER:
        case SCOPE_COOKIE:
        case SCOPE_QUERYPARAM:
            map = acquireMapForScope(scope);
            break;
        default:
            map = super.getScopeDataMapCopy(scope);
        }
        return map;
    }

}
