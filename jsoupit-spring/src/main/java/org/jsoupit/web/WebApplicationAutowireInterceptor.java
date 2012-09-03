package org.jsoupit.web;

import java.util.ArrayList;
import java.util.List;

import org.jsoupit.template.snippet.interceptor.ContextDataAutowireInterceptor;
import org.jsoupit.template.snippet.interceptor.ContextDataConvertor;
import org.jsoupit.web.convertor.String2Int;

public class WebApplicationAutowireInterceptor extends ContextDataAutowireInterceptor {

    public WebApplicationAutowireInterceptor() {
        setConvertorList(new ArrayList<ContextDataConvertor>());
    }

    private List<ContextDataConvertor> getDefaultList() {
        List<ContextDataConvertor> defaultList = new ArrayList<>();
        defaultList.add(new String2Int());
        return defaultList;
    }

    @Override
    public void setConvertorList(List<ContextDataConvertor> convertorList) {
        List<ContextDataConvertor> list = new ArrayList<>(convertorList);
        list.addAll(getDefaultList());
        super.setConvertorList(list);
    }

}
