/*
 * Copyright 2014 astamuse company,Ltd.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */
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
        return cls.newInstance();
    }

    @Override
    protected TemplateInfo loadResource(String name) {
        throw new UnsupportedOperationException();
    }

}
