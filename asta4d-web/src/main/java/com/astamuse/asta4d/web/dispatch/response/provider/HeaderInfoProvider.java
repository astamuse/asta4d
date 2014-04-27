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

package com.astamuse.asta4d.web.dispatch.response.provider;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import com.astamuse.asta4d.web.dispatch.mapping.UrlMappingRule;

public class HeaderInfoProvider implements ContentProvider {

    private HashMap<String, String> headerMap;

    private List<Cookie> cookieList;

    private Integer status;

    private boolean continuable = true;

    public HeaderInfoProvider() {
        this(null);
    }

    public HeaderInfoProvider(Integer status) {
        this.status = status;
        headerMap = new HashMap<>();
        cookieList = new ArrayList<>();
    }

    public HeaderInfoProvider(Integer status, boolean continuable) {
        this.status = status;
        this.continuable = continuable;
        headerMap = new HashMap<>();
        cookieList = new ArrayList<>();
    }

    public Integer getStatus() {
        return status;
    }

    public void addHeader(String name, String value) {
        headerMap.put(name, value);
    }

    public void addCookie(String name, String value) {
        cookieList.add(new Cookie(name, value));
    }

    public void addCookie(Cookie cookie) {
        cookieList.add(cookie);
    }

    public HashMap<String, String> getHeaderMap() {
        return headerMap;
    }

    public List<Cookie> getCookieList() {
        return cookieList;
    }

    @Override
    public boolean isContinuable() {
        return continuable;
    }

    public void setContinuable(boolean continuable) {
        this.continuable = continuable;
    }

    @Override
    public void produce(UrlMappingRule currentRule, HttpServletResponse response) throws Exception {
        if (status != null) {
            response.setStatus(status);
        }

        Set<Entry<String, String>> headers = headerMap.entrySet();
        for (Entry<String, String> h : headers) {
            response.addHeader(h.getKey(), h.getValue());
        }

        for (Cookie c : cookieList) {
            response.addCookie(c);
        }
    }
}
