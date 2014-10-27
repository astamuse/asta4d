package com.astamuse.asta4d.sample;

import com.astamuse.asta4d.sample.interceptor.SamplePageInterceptor;
import com.astamuse.asta4d.sample.interceptor.SampleSnippetInterceptor;
import com.astamuse.asta4d.snippet.DefaultSnippetInvoker;
import com.astamuse.asta4d.web.WebApplicationConfiguration;
import com.astamuse.asta4d.web.servlet.Asta4dServlet;

public class Asta4DSampleServlet extends Asta4dServlet {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    @Override
    protected WebApplicationConfiguration createConfiguration() {
        WebApplicationConfiguration conf = super.createConfiguration();

        conf.getPageInterceptorList().add(new SamplePageInterceptor());

        DefaultSnippetInvoker snippetInvoker = ((DefaultSnippetInvoker) conf.getSnippetInvoker());
        snippetInvoker.getSnippetInterceptorList().add(new SampleSnippetInterceptor());

        return conf;
    }

}
