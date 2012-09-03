package org.jsoupit.web.servlet;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;


/**
 * Here we are going to implement a view first mechanism of view resolving. We
 * need a url mapping algorithm too.
 * 
 * @author xzer
 * 
 */
public class JsoupitFilter implements Filter {

    @Override
    public void destroy() {
        // do nothing
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp,
            FilterChain chain) throws IOException, ServletException {
        new MyTestPage().output(resp.getOutputStream());

    }

    @Override
    public void init(FilterConfig conf) throws ServletException {
        // do nothing
    }

}
