package com.astamuse.asta4d.web.dispatch.response.writer;

import javax.servlet.http.HttpServletResponse;

import com.astamuse.asta4d.Context;
import com.astamuse.asta4d.web.WebApplicationContext;
import com.astamuse.asta4d.web.dispatch.mapping.UrlMappingRule;
import com.astamuse.asta4d.web.dispatch.response.provider.RedirectDescriptor;
import com.astamuse.asta4d.web.util.RedirectUtil;

public class RedirectActionWriter implements ContentWriter<RedirectDescriptor> {
    @Override
    public void writeResponse(UrlMappingRule currentRule, HttpServletResponse response, RedirectDescriptor content) throws Exception {
        RedirectDescriptor rd = (RedirectDescriptor) content;
        String url = rd.getTargetPath();
        if (url.startsWith("/")) {
            WebApplicationContext context = (WebApplicationContext) Context.getCurrentThreadContext();
            url = context.getRequest().getContextPath() + url;
        }
        url = RedirectUtil.setFlashScopeData(url, rd.getFlashScopeData());
        response.sendRedirect(url);
    }

}
