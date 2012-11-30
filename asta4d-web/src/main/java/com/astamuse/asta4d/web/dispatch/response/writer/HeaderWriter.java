package com.astamuse.asta4d.web.dispatch.response.writer;

import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import com.astamuse.asta4d.web.dispatch.mapping.UrlMappingRule;
import com.astamuse.asta4d.web.dispatch.response.provider.HeaderInfo;

public class HeaderWriter implements ContentWriter<HeaderInfo> {

    @Override
    public void writeResponse(UrlMappingRule currentRule, HttpServletResponse response, HeaderInfo info) throws Exception {
        if (info == null) {
            return;
        }
        Integer status = info.getStatus();
        if (status != null) {
            response.setStatus(status);
        }

        Set<Entry<String, String>> headers = info.getHeaderMap().entrySet();
        for (Entry<String, String> h : headers) {
            response.addHeader(h.getKey(), h.getValue());
        }

        List<Cookie> cookies = info.getCookieList();
        for (Cookie c : cookies) {
            response.addCookie(c);
        }

    }

}
