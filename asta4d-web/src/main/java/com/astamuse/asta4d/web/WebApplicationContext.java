/*
 * Copyright 2012 astamuse company,Ltd.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */

package com.astamuse.asta4d.web;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.astamuse.asta4d.Context;
import com.astamuse.asta4d.ContextMap;
import com.astamuse.asta4d.util.DelegatedContextMap;
import com.astamuse.asta4d.util.UnmodifiableContextMap;
import com.astamuse.asta4d.web.dispatch.mapping.UrlMappingRule;
import com.astamuse.asta4d.web.util.context.SessionMap;

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

    private final static String SAVEKEY_SERVLET_CONTEXT = WebApplicationContext.class.getName() + "##SAVEKEY-SERVLET-CONTEXT";

    private final static String SAVEKEY_ACCESS_URI = WebApplicationContext.class.getName() + "##SAVEKEY-ACCESS-URI";

    public final static String SAVEKEY_CURRENT_RULE = WebApplicationContext.class.getName() + "##SAVEKEY_CURRENT_RULE";

    public final static WebApplicationContext getCurrentThreadWebApplicationContext() {
        return Context.getCurrentThreadContext();
    }

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

    public ServletContext getServletContext() {
        return this.getData(SAVEKEY_SERVLET_CONTEXT);
    }

    public void setServletContext(ServletContext servletContext) {
        this.setData(SAVEKEY_SERVLET_CONTEXT, servletContext);
    }

    public void setAccessURI(String uri) {
        this.setData(SAVEKEY_ACCESS_URI, uri);
    }

    public String getAccessURI() {
        return this.getData(SAVEKEY_ACCESS_URI);
    }

    public void setCurrentRule(UrlMappingRule rule) {
        this.setData(SAVEKEY_CURRENT_RULE, rule);
    }

    public UrlMappingRule getCurrentRule() {
        return this.getData(SAVEKEY_CURRENT_RULE);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected ContextMap createMapForScope(String scope) {
        ContextMap map = null;
        switch (scope) {
        case SCOPE_SESSION: {
            HttpServletRequest request = getRequest();
            map = new SessionMap(request);
        }
            break;
        case SCOPE_HEADER: {
            HttpServletRequest request = getRequest();
            map = DelegatedContextMap.createByNonThreadSafeHashMap();
            Enumeration<String> headerNames = request.getHeaderNames();
            while (headerNames.hasMoreElements()) {
                String name = headerNames.nextElement();
                String[] values = getHeaderValues(request.getHeaders(name));
                map.put(name, values);
            }
            map = new UnmodifiableContextMap(map);
        }
            break;
        case SCOPE_COOKIE: {
            HttpServletRequest request = getRequest();
            map = DelegatedContextMap.createByNonThreadSafeHashMap();
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    String name = cookie.getName();
                    String[] values = mergeCookieValues(cookie, (String[]) map.get(name));
                    map.put(name, values);
                }
            }
            map = new UnmodifiableContextMap(map);
        }
            break;

        case SCOPE_REQUEST:
            map = super.acquireMapForScope(SCOPE_DEFAULT);
            break;
        case SCOPE_QUERYPARAM_ALIAS:
        case SCOPE_QUERYPARAM: {
            HttpServletRequest request = getRequest();
            map = DelegatedContextMap.createByNonThreadSafeHashMap();
            Enumeration<String> paramNames = request.getParameterNames();
            while (paramNames.hasMoreElements()) {
                String name = paramNames.nextElement();
                String[] values = request.getParameterValues(name);
                map.put(name, values);
            }
            map = new UnmodifiableContextMap(map);
        }
            break;
        case SCOPE_PATHVAR:
            map = DelegatedContextMap.createByNonThreadSafeHashMap();
            break;
        default:
            map = super.createMapForScope(scope);
        }
        return map;
    }

    public Context clone() {
        Context newCtx = new WebApplicationContext();
        copyScopesTo(newCtx);
        return newCtx;
    }

    private static String[] getHeaderValues(Enumeration<String> headers) {
        List<String> values = new ArrayList<>();
        while (headers.hasMoreElements()) {
            values.add(headers.nextElement());
        }
        return values.toArray(new String[values.size()]);
    }

    private static String[] mergeCookieValues(Cookie cookie, String[] cookies) {
        if (cookies == null) {
            return new String[] { cookie.getValue() };
        }
        String[] cookieValues = new String[cookies.length + 1];
        System.arraycopy(cookies, 0, cookieValues, 0, cookies.length);
        cookieValues[cookieValues.length - 1] = cookie.getValue();
        return cookieValues;
    }

}
