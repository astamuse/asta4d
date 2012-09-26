package com.astamuse.asta4d.misc.spring.mvc;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.View;

import com.astamuse.asta4d.template.TemplateException;
import com.astamuse.asta4d.web.WebPage;

public class SpringWebPageView implements View {

    @SuppressWarnings("unused")
    private String path = "";

    private WebPage page = null;

    public SpringWebPageView(String path) throws TemplateException {
        super();
        this.path = path;
        // TODO handle page not found (rewrite it for internationalization and
        // negotiation view)
        this.page = new WebPage(path);
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
