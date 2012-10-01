package com.astamuse.asta4d.misc.spring.mvc;

import java.util.Locale;

import org.springframework.web.servlet.View;
import org.springframework.web.servlet.ViewResolver;

import com.astamuse.asta4d.web.view.WebPageView;

public class SpringWebPageViewResolver implements ViewResolver {

    @Override
    public View resolveViewName(String viewName, Locale locale) throws Exception {
        WebPageView view = new WebPageView(viewName);
        return ConvertUtil.convertSpringView(view);
    }

}
