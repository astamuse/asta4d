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

package com.astamuse.asta4d.web.dispatch.mapping.ext;

import java.util.List;

import com.astamuse.asta4d.web.dispatch.mapping.UrlMappingRule;
import com.astamuse.asta4d.web.dispatch.request.func.ro.RequestHandlerObjIntf;
import com.astamuse.asta4d.web.dispatch.request.func.ro.RequestHandlerObjIntf1;
import com.astamuse.asta4d.web.dispatch.request.func.ro.RequestHandlerObjIntf2;
import com.astamuse.asta4d.web.dispatch.request.func.ro.RequestHandlerObjIntf3;
import com.astamuse.asta4d.web.util.bean.DeclareInstanceUtil;

public class HandyRuleWithHandler extends HandyRuleWithForward {

    public HandyRuleWithHandler(UrlMappingRule rule) {
        super(rule);
    }

    // There is a eclipse bug, we are waiting for the fix
    /*
    public HandyRuleWithHandler handler(RequestHandlerVoidIntf handler) {
        return this;
    }

    public <T> HandyRuleWithHandler handler(RequestHandlerVoidIntf1<T> handler) {
        return this;
    }

    public <T1, T2> HandyRuleWithHandler handler(RequestHandlerVoidIntf2<T1, T2> handler) {
        return this;
    }
    */

    public HandyRuleWithHandler handler(RequestHandlerObjIntf handler) {
        return _handler(handler);
    }

    public <T> HandyRuleWithHandler handler(RequestHandlerObjIntf1<T> handler) {
        return _handler(handler);
    }

    public <T1, T2> HandyRuleWithHandler handler(RequestHandlerObjIntf2<T1, T2> handler) {
        return _handler(handler);
    }

    public <T1, T2, T3> HandyRuleWithHandler handler(RequestHandlerObjIntf3<T1, T2, T3> handler) {
        return _handler(handler);
    }

    public HandyRuleWithHandler handler(Object handler) {
        return _handler(handler);
    }

    private HandyRuleWithHandler _handler(Object handler) {
        List<Object> list = rule.getHandlerList();
        list.add(DeclareInstanceUtil.createInstance((handler)));
        return this;
    }

}
