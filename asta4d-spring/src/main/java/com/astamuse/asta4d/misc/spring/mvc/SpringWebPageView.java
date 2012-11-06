package com.astamuse.asta4d.misc.spring.mvc;

import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.View;

import com.astamuse.asta4d.Context;
import com.astamuse.asta4d.template.TemplateException;
import com.astamuse.asta4d.web.dispatch.response.provider.Asta4DPageProvider;

public class SpringWebPageView implements View {

    private Asta4DPageProvider templateProvider;

    public SpringWebPageView(Asta4DPageProvider templateProvider) throws TemplateException {
        super();
        this.templateProvider = templateProvider;
    }

    @Override
    public String getContentType() {
        return "";
    }

    @Override
    public void render(Map<String, ?> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Context context = Context.getCurrentThreadContext();
        for (Entry<String, ?> entry : model.entrySet()) {
            context.setData(entry.getKey(), entry.getValue());
        }
        // templateProvider.produce(response);
    }

}
