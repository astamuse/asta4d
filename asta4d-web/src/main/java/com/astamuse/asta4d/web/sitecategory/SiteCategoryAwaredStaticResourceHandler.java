package com.astamuse.asta4d.web.sitecategory;

import java.io.FileNotFoundException;
import java.io.IOException;

import javax.servlet.ServletContext;

import com.astamuse.asta4d.web.WebApplicationContext;
import com.astamuse.asta4d.web.builtin.StaticResourceHandler;

public class SiteCategoryAwaredStaticResourceHandler extends StaticResourceHandler {

    private SiteCategoryAwaredResourceLoader<StaticFileInfo> resourceLoader = new SiteCategoryAwaredResourceLoader<StaticFileInfo>() {
        @Override
        public StaticFileInfo load(String path, Object extraInfomation) throws Exception {
            return _super_retrieveStaticFileInfo(WebApplicationContext.getCurrentThreadWebApplicationContext().getServletContext(), path);
        }
    };

    public SiteCategoryAwaredStaticResourceHandler() {
        super();
    }

    public SiteCategoryAwaredStaticResourceHandler(String basePath) {
        super(basePath);
    }

    private StaticFileInfo _super_retrieveStaticFileInfo(ServletContext servletContext, String path) throws FileNotFoundException,
            IOException {
        return super.retrieveStaticFileInfo(servletContext, path);
    }

    @Override
    protected StaticFileInfo retrieveStaticFileInfo(ServletContext servletContext, String path) throws FileNotFoundException, IOException {
        String[] categories = SiteCategoryUtil.getCurrentRequestSearchCategories();
        try {
            return resourceLoader.load(categories, path);
        } catch (Exception e) {
            throw new IOException(e);
        }
    }

}
