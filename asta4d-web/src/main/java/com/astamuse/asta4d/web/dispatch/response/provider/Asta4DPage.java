package com.astamuse.asta4d.web.dispatch.response.provider;

import java.io.OutputStream;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import com.astamuse.asta4d.Page;

public class Asta4DPage {

    protected final static String DEFAULT_CONTENT_TYPE = "text/html; charset=UTF-8";

    protected Page page;

    protected boolean bodyOnly;

    public Asta4DPage(String path) throws Exception {
        this(path, false);
    }

    public Asta4DPage(String path, boolean bodyOnly) throws Exception {
        this.page = new Page(path);
        this.bodyOnly = bodyOnly;
    }

    public String getContentType() {
        Document doc = page.getRenderedDocument();
        Elements elems = doc.select("meta[http-equiv=Content-Type]");
        if (elems.size() == 0) {
            return DEFAULT_CONTENT_TYPE;
        } else {
            return elems.get(0).attr("content");
        }
    }

    public void output(OutputStream out) throws Exception {
        Document doc = page.getRenderedDocument();
        String s = bodyOnly ? doc.body().html() : doc.outerHtml();
        out.write(s.getBytes("utf-8"));
    }

}
