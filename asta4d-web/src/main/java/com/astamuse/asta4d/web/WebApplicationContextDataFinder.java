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
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.astamuse.asta4d.Context;
import com.astamuse.asta4d.data.DataOperationException;
import com.astamuse.asta4d.data.DefaultContextDataFinder;
import com.astamuse.asta4d.web.dispatch.RequestDispatcher;
import com.astamuse.asta4d.web.dispatch.mapping.UrlMappingRule;

public class WebApplicationContextDataFinder extends DefaultContextDataFinder {

    public WebApplicationContextDataFinder() {
        List<String> dataScopeOrder = new ArrayList<>();
        dataScopeOrder.add(WebApplicationContext.SCOPE_ATTR);
        dataScopeOrder.add(WebApplicationContext.SCOPE_PATHVAR);
        dataScopeOrder.add(WebApplicationContext.SCOPE_QUERYPARAM);
        dataScopeOrder.add(WebApplicationContext.SCOPE_FLASH);
        dataScopeOrder.add(WebApplicationContext.SCOPE_COOKIE);
        dataScopeOrder.add(WebApplicationContext.SCOPE_HEADER);
        dataScopeOrder.add(WebApplicationContext.SCOPE_REQUEST);
        dataScopeOrder.add(WebApplicationContext.SCOPE_SESSION);
        dataScopeOrder.add(WebApplicationContext.SCOPE_GLOBAL);
        this.setDataSearchScopeOrder(dataScopeOrder);
    }

    @Override
    public Object findDataInContext(Context context, String scope, String name, Class<?> type) throws DataOperationException {
        // TODO class equals is not reliable
        if (type.equals(HttpServletRequest.class)) {
            return ((WebApplicationContext) context).getRequest();
        } else if (type.equals(HttpServletResponse.class)) {
            return ((WebApplicationContext) context).getResponse();
        } else if (type.equals(UrlMappingRule.class)) {
            return context.getData(RequestDispatcher.KEY_CURRENT_RULE);
        } else {
            return super.findDataInContext(context, scope, name, type);
        }
    }
}
