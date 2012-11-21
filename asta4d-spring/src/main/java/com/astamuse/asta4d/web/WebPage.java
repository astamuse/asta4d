package com.astamuse.asta4d.web;

import java.io.OutputStream;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import com.astamuse.asta4d.Page;

public class WebPage extends Page {

    protected final static String DEFAULT_CONTENT_TYPE = "text/html; charset=UTF-8";

    protected String contentType;

    protected boolean bodyOnly;

    public WebPage(String path) throws Exception {
        this(path, false);
    }

    public WebPage(String path, boolean bodyOnly) throws Exception {
        super(path);
        Document doc = getRenderedDocument();
        Elements elems = doc.select("meta[http-equiv=Content-Type]");
        if (elems.size() == 0) {
            this.contentType = DEFAULT_CONTENT_TYPE;
        } else {
            this.contentType = elems.get(0).attr("content");
        }
        this.bodyOnly = bodyOnly;
    }

    public String getContentType() {
        return contentType;
    }

    public void output(OutputStream out) throws Exception {
        Document doc = getRenderedDocument();
        String s = bodyOnly ? doc.body().html() : doc.outerHtml();
        out.write(s.getBytes("utf-8"));
    }

}
