package com.astamuse.asta4d.web.sitecategory;

import org.apache.commons.lang3.StringUtils;

import com.astamuse.asta4d.template.Template;
import com.astamuse.asta4d.template.TemplateException;
import com.astamuse.asta4d.template.TemplateNotFoundException;
import com.astamuse.asta4d.template.TemplateResolver;

public class SiteCategoryAwaredTemplateResolver extends TemplateResolver {

    private TemplateResolver underlineTemplateResolver = null;

    private SiteCategoryAwaredResourceLoader<Template> siteCategoryAwaredResourceLoader = new SiteCategoryAwaredResourceLoader<Template>() {
        @Override
        public Template load(String path) throws Exception {
            try {
                return underlineTemplateResolver.findTemplate(path);
            } catch (TemplateNotFoundException ex) {
                return null;
            }
        }
    };

    public SiteCategoryAwaredTemplateResolver() {

    }

    public SiteCategoryAwaredTemplateResolver(TemplateResolver underlineTemplateResolver) {
        this.underlineTemplateResolver = underlineTemplateResolver;
    }

    public TemplateResolver getUnderlineTemplateResolver() {
        return underlineTemplateResolver;
    }

    public void setUnderlineTemplateResolver(TemplateResolver underlineTemplateResolver) {
        this.underlineTemplateResolver = underlineTemplateResolver;
    }

    @Override
    public Template findTemplate(String path) throws TemplateException, TemplateNotFoundException {

        try {
            String[] categories = SiteCategoryUtil.getCurrentRequestSearchCategories();
            Template template = siteCategoryAwaredResourceLoader.load(categories, path);
            if (template == null) {
                throw new TemplateNotFoundException(path, "(in all the categories:" + StringUtils.join(categories, ",") + ")");
            } else {
                return template;
            }
        } catch (TemplateNotFoundException e) {
            throw e;
        } catch (TemplateException e) {
            throw e;
        } catch (Exception e) {
            throw new TemplateException(e);
        }
    }

    @Override
    protected TemplateInfo loadResource(String name) {
        throw new UnsupportedOperationException();
    }

}
