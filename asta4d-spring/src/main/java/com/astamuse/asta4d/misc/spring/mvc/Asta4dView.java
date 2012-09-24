package com.astamuse.asta4d.misc.spring.mvc;

import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.View;

import com.astamuse.asta4d.template.TemplateException;
import com.astamuse.asta4d.web.WebPage;

public class Asta4dView implements View {

    @SuppressWarnings("unused")
    private String path = "";

    @SuppressWarnings("unused")
    private Locale locale = Locale.getDefault();

    private WebPage page = null;

    public Asta4dView(String path, Locale locale) throws TemplateException {
        super();
        this.path = path;
        this.locale = locale;
        // TODO handle page not found (rewrite it for internationalization and
        // negotiation view)
        this.page = new WebPage(path, locale);
    }

    @Override
    public String getContentType() {
        return page.getContentType();
    }

    @Override
    public void render(Map<String, ?> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
        // TODO should we merge the model data?
        page.output(response.getOutputStream());
    }

}
