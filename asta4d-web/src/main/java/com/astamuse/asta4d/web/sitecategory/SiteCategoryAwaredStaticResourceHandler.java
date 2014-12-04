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
