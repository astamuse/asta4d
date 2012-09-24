package com.astamuse.asta4d.misc.spring.mvc;

import java.util.Locale;

import org.springframework.web.servlet.View;
import org.springframework.web.servlet.ViewResolver;

import com.astamuse.asta4d.template.TemplateException;

public class Asta4dViewResolver implements ViewResolver {

    @Override
    public View resolveViewName(String viewName, Locale locale) throws Exception {
        try {
            return new Asta4dView(viewName, locale);
        } catch (TemplateException e) {
            return null;
        }
    }

}
