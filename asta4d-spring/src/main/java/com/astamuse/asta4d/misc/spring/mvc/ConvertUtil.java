package com.astamuse.asta4d.misc.spring.mvc;

import org.springframework.web.servlet.View;

import com.astamuse.asta4d.template.TemplateException;
import com.astamuse.asta4d.web.view.Asta4dView;
import com.astamuse.asta4d.web.view.RedirectView;
import com.astamuse.asta4d.web.view.WebPageView;

public class ConvertUtil {

    public static View convertSpringView(Asta4dView view) throws TemplateException {
        if (view instanceof WebPageView) {
            WebPageView pageView = (WebPageView) view;
            return new SpringWebPageView(pageView.getPath());
        }
        if (view instanceof RedirectView) {
            RedirectView redirectView = (RedirectView) view;
            return new org.springframework.web.servlet.view.RedirectView(redirectView.getUrl(), redirectView.isContextRelative(),
                    redirectView.isHttp10Compatible(), redirectView.isExposeModelAttributes());
        }
        throw new UnsupportedOperationException("View Type:" + view.getClass().getName());
    }

    private ConvertUtil() {
    }
}
