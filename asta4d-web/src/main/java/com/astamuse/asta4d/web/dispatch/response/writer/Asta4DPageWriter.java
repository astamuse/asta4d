package com.astamuse.asta4d.web.dispatch.response.writer;

import javax.servlet.http.HttpServletResponse;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import com.astamuse.asta4d.Page;
import com.astamuse.asta4d.web.dispatch.mapping.UrlMappingRule;

public class Asta4DPageWriter implements ContentWriter<Page> {

    public final static String AttrBodyOnly = Asta4DPageWriter.class.getName() + "##bodyOnly";

    protected final static String DEFAULT_CONTENT_TYPE = "text/html; charset=UTF-8";

    public String getContentType(Document doc) {
        Elements elems = doc.select("meta[http-equiv=Content-Type]");
        if (elems.size() == 0) {
            return DEFAULT_CONTENT_TYPE;
        } else {
            return elems.get(0).attr("content");
        }
    }

    @Override
    public void writeResponse(UrlMappingRule currentRule, HttpServletResponse response, Page page) throws Exception {
        Document doc = page.getRenderedDocument();

        response.setContentType(getContentType(doc));

        // TODO we should try to retrieve the content type
        if (currentRule.hasAttribute(AttrBodyOnly)) {
            response.getOutputStream().write(doc.body().html().getBytes("UTF-8"));
        } else {
            response.getOutputStream().write(doc.outerHtml().getBytes("UTF-8"));
        }

    }

}
