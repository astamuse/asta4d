package org.jsoupit.misc.spring.mvc;

import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jsoupit.template.TemplateException;
import org.jsoupit.web.WebPage;
import org.springframework.web.servlet.View;

public class JsoupitView implements View {

    @SuppressWarnings("unused")
    private String path = "";

    @SuppressWarnings("unused")
    private Locale locale = Locale.getDefault();

    private WebPage page = null;

    public JsoupitView(String path, Locale locale) {
        super();
        this.path = path;
        this.locale = locale;
        // TODO handle page not found
        try {
            this.page = new WebPage(path, locale);
        } catch (TemplateException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getContentType() {
        return page.getContentType();
    }

    @Override
    public void render(Map<String, ?> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
        // TODO should we merge the model data?
        // TODO post rendering
        page.output(response.getOutputStream());
    }

}
