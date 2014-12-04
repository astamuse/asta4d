/*
 * Copyright 2014 astamuse company,Ltd.
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
