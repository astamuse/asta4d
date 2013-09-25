package com.astamuse.asta4d.web.util.context;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.astamuse.asta4d.ContextMap;

public class SessionMap implements ContextMap {

    private HttpServletRequest request;

    public SessionMap(HttpServletRequest request) {
        this.request = request;
    }

    @Override
    public void put(String key, Object data) {
        request.getSession(true).setAttribute(key, data);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T get(String key) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return null;
        } else {
            return (T) session.getAttribute(key);
        }
    }

    @Override
    public ContextMap createClone() {
        return this;
    }

}
