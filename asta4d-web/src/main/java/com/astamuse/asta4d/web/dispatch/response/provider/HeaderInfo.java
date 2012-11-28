package com.astamuse.asta4d.web.dispatch.response.provider;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.Cookie;

public class HeaderInfo {

    private HashMap<String, String> headerMap;

    private List<Cookie> cookieList;

    private Integer status;

    public HeaderInfo() {
        this(null);
    }

    public HeaderInfo(Integer status) {
        this.status = status;
        headerMap = new HashMap<>();
        cookieList = new ArrayList<>();
    }

    public Integer getStatus() {
        return status;
    }

    public void setHeader(String name, String value) {
        headerMap.put(name, value);
    }

    public void setCookie(String name, String value) {
        cookieList.add(new Cookie(name, value));
    }

    public void setCookie(Cookie cookie) {
        cookieList.add(cookie);
    }

    public HashMap<String, String> getHeaderMap() {
        return headerMap;
    }

    public List<Cookie> getCookieList() {
        return cookieList;
    }

}
