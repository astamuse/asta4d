package com.astamuse.asta4d.web.dispatch.response;

import javax.servlet.http.HttpServletResponse;

import com.astamuse.asta4d.Context;
import com.astamuse.asta4d.web.WebApplicationContext;
import com.astamuse.asta4d.web.dispatch.response.provider.RedirectTargetProvider;
import com.astamuse.asta4d.web.dispatch.response.provider.RedirectTargetProvider.RedirectDescriptor;
import com.astamuse.asta4d.web.util.RedirectUtil;

public class RedirectActionWriter implements ContentWriter {
    @Override
    public void writeResponse(HttpServletResponse response, Object content) throws Exception {
        RedirectTargetProvider.RedirectDescriptor rd = (RedirectDescriptor) content;
        String url = rd.getTargetPath();
        if (url.startsWith("/")) {
            WebApplicationContext context = (WebApplicationContext) Context.getCurrentThreadContext();
            url = context.getRequest().getContextPath() + url;
        }
        url = RedirectUtil.setFlashScopeData(url, rd.getFlashScopeData());
        response.sendRedirect(url);
    }

}
