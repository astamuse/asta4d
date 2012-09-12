package com.astamuse.asta4d.web;

import java.util.Locale;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import com.astamuse.asta4d.Page;
import com.astamuse.asta4d.template.TemplateException;

public class WebPage extends Page {

    protected final static String DEFAULT_CONTENT_TYPE = "text/html; charset=UTF-8";

    protected String contentType;

    public WebPage(String path, Locale locale) throws TemplateException {
        super(path);
        Document doc = template.getDocumentClone();
        Elements elems = doc.select("meta[http-equiv=Content-Type]");
        if (elems.size() == 0) {
            this.contentType = DEFAULT_CONTENT_TYPE;
        } else {
            this.contentType = elems.get(0).attr("content");
        }
    }

    public String getContentType() {
        return contentType;
    }

}
