package com.astamuse.asta4d.web;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.astamuse.asta4d.Context;
import com.astamuse.asta4d.data.DefaultContextDataFinder;

public class WebApplicationContextDataFinder extends DefaultContextDataFinder {

    public WebApplicationContextDataFinder() {
        List<String> dataScopeOrder = new ArrayList<>();
        dataScopeOrder.add(WebApplicationContext.SCOPE_ATTR);
        dataScopeOrder.add(WebApplicationContext.SCOPE_PATHVAR);
        dataScopeOrder.add(WebApplicationContext.SCOPE_QUERYPARAM);
        dataScopeOrder.add(WebApplicationContext.SCOPE_COOKIE);
        dataScopeOrder.add(WebApplicationContext.SCOPE_HEADER);
        dataScopeOrder.add(WebApplicationContext.SCOPE_REQUEST);
        dataScopeOrder.add(WebApplicationContext.SCOPE_SESSION);
        dataScopeOrder.add(WebApplicationContext.SCOPE_GLOBAL);
        this.setDataSearchScopeOrder(dataScopeOrder);
    }

    @Override
    public Object findDataInContext(Context context, String scope, String name, Class<?> type) {
        if (type.equals(HttpServletRequest.class)) {
            return ((WebApplicationContext) context).getRequest();
        } else {
            return super.findDataInContext(context, scope, name, type);
        }
    }

}
