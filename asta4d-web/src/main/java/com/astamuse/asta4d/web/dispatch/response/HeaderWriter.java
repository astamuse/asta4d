package com.astamuse.asta4d.web.dispatch.response;

import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import com.astamuse.asta4d.web.dispatch.response.provider.HeaderInfoHoldingContent;

public class HeaderWriter implements ContentWriter {

    private HeaderInfoHoldingContent headerInfo = null;

    public HeaderWriter() {

    }

    public HeaderWriter(HeaderInfoHoldingContent headerInfo) {
        this.headerInfo = headerInfo;
    }

    @Override
    public void writeResponse(HttpServletResponse response, Object content) throws Exception {
        HeaderInfoHoldingContent info = headerInfo == null ? (HeaderInfoHoldingContent) content : headerInfo;
        if (info == null) {
            return;
        }
        Integer status = info.getStatus();
        if (status == null) {
            response.setStatus((Integer) content);
        } else {
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
