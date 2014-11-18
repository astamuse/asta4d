package com.astamuse.asta4d.web.sitecategory;

import javax.servlet.ServletContext;

import org.apache.commons.lang3.StringUtils;

import com.astamuse.asta4d.template.Template;
import com.astamuse.asta4d.template.TemplateException;
import com.astamuse.asta4d.template.TemplateNotFoundException;
import com.astamuse.asta4d.template.TemplateResolver;
import com.astamuse.asta4d.util.MemorySafeResourceCache;
import com.astamuse.asta4d.util.MemorySafeResourceCache.ResouceHolder;
import com.astamuse.asta4d.web.WebApplicationContext;
import com.astamuse.asta4d.web.WebApplicationTemplateResolver;

public class SiteCategoryAwaredTemplateResolver extends TemplateResolver {

    private TemplateResolver legacy_underlineTemplateResolver = null;

    private Class<? extends TemplateResolver> underlineTemplateResolverCls = null;

    private MemorySafeResourceCache<Object, TemplateResolver> underlineTemplateResolverCache = new MemorySafeResourceCache<>();

    private SiteCategoryAwaredResourceLoader<Template> siteCategoryAwaredResourceLoader = new SiteCategoryAwaredResourceLoader<Template>() {
        @Override
        public Template load(String path, Object extraInfomation) throws Exception {
            try {
                TemplateResolver underlineTemplateResolver = null;
                ResouceHolder<TemplateResolver> rh = underlineTemplateResolverCache.get(extraInfomation);
                if (rh == null) {
                    underlineTemplateResolver = createUnderlineTemplateResolverInstance(underlineTemplateResolverCls);
                    if (underlineTemplateResolver instanceof WebApplicationTemplateResolver) {
                        ServletContext sc = WebApplicationContext.getCurrentThreadWebApplicationContext().getServletContext();
                        ((WebApplicationTemplateResolver) underlineTemplateResolver).setServletContext(sc);
                    }
                    underlineTemplateResolverCache.put(extraInfomation, underlineTemplateResolver);
                } else {
                    underlineTemplateResolver = rh.get();// there must be
                }
                return underlineTemplateResolver.findTemplate(path);
            } catch (TemplateNotFoundException ex) {
                return null;
            }
        }
    };

    /**
     * will be removed when release
     * 
     * @param underlineTemplateResolver
     */
    @Deprecated
    public SiteCategoryAwaredTemplateResolver(TemplateResolver legacy_underlineTemplateResolver) {
        this.legacy_underlineTemplateResolver = legacy_underlineTemplateResolver;
    }

    public SiteCategoryAwaredTemplateResolver() {
        this(WebApplicationTemplateResolver.class);
    }

    public SiteCategoryAwaredTemplateResolver(Class<? extends TemplateResolver> underlineTemplateResolverCls) {
        this.underlineTemplateResolverCls = underlineTemplateResolverCls;
    }

    public Class<? extends TemplateResolver> getUnderlineTemplateResolver() {
        return underlineTemplateResolverCls;
    }

    public void setUnderlineTemplateResolver(Class<? extends TemplateResolver> underlineTemplateResolverCls) {
        this.underlineTemplateResolverCls = underlineTemplateResolverCls;
    }

    @Override
    public Template findTemplate(final String path) throws TemplateException, TemplateNotFoundException {

        try {
            final String[] categories = SiteCategoryUtil.getCurrentRequestSearchCategories();
            String categoryKey = createCategoryKey(categories);
            Template template = siteCategoryAwaredResourceLoader.load(categories, path, categoryKey);
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

    protected String createCategoryKey(String[] categories) {
        return StringUtils.join(categories, ",");
    }

    protected TemplateResolver createUnderlineTemplateResolverInstance(Class<? extends TemplateResolver> cls) throws Exception {
        if (legacy_underlineTemplateResolver == null) {
            return cls.newInstance();
        } else {
            return legacy_underlineTemplateResolver;
        }
    }

    @Override
    protected TemplateInfo loadResource(String name) {
        throw new UnsupportedOperationException();
    }

}
