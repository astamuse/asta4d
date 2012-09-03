package org.jsoupit.web;

import java.util.ArrayList;
import java.util.List;

import org.jsoupit.template.snippet.DefaultSnippetInvoker;
import org.jsoupit.template.snippet.interceptor.SnippetInterceptor;

public class WebApplicationSnippetInvoker extends DefaultSnippetInvoker {

    public WebApplicationSnippetInvoker() {
        setSnippetInterceptorList(new ArrayList<SnippetInterceptor>());
    }

    @Override
    public void setSnippetInterceptorList(List<SnippetInterceptor> snippetInterceptorList) {
        List<SnippetInterceptor> list = new ArrayList<>(snippetInterceptorList);
        // TODO iterate to confirm if there is already an autowire interceptor
        list.add(new WebApplicationAutowireInterceptor());
        super.setSnippetInterceptorList(list);
    }

}
