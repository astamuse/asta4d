package com.astamuse.asta4d.sample;

import java.util.Locale;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import org.apache.commons.lang3.StringUtils;

import com.astamuse.asta4d.sample.interceptor.SamplePageInterceptor;
import com.astamuse.asta4d.sample.interceptor.SampleSnippetInterceptor;
import com.astamuse.asta4d.snippet.DefaultSnippetInvoker;
import com.astamuse.asta4d.util.i18n.LocalizeUtil;
import com.astamuse.asta4d.web.WebApplicationConfiguration;
import com.astamuse.asta4d.web.WebApplicationContext;
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

    @Override
    public void init(ServletConfig config) throws ServletException {
        // for a international application, we use root as default locale
        Locale.setDefault(Locale.ROOT);
        super.init(config);
    }

    @Override
    protected void service() throws Exception {
        // resolve the locale of current request
        // for a formal web application, the locale should be resolved from the head sent by client browser,
        // but as a sample project, we use a simple way to decide the locale to simplify the logic

        WebApplicationContext context = WebApplicationContext.getCurrentThreadWebApplicationContext();
        String locale = context.getRequest().getParameter("locale");
        if (StringUtils.isNotEmpty(locale)) {
            context.setCurrentLocale(LocalizeUtil.getLocale(locale));
        }
        super.service();
    }

}
