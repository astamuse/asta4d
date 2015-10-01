/*
 * Copyright 2012 astamuse company,Ltd.
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

package com.astamuse.asta4d.web;

import javax.servlet.ServletContext;

import com.astamuse.asta4d.template.AbstractTemplateResolver;

public class WebApplicationTemplateResolver extends AbstractTemplateResolver {

    private ServletContext servletContext;

    public WebApplicationTemplateResolver() {
        super();
    }

    public WebApplicationTemplateResolver(ServletContext servletContext) {
        super();
        this.servletContext = servletContext;
    }

    public ServletContext getServletContext() {
        return servletContext;
    }

    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    @Override
    protected TemplateInfo loadResource(String path) {
        if (servletContext == null) {
            ServletContext sc = WebApplicationContext.getCurrentThreadWebApplicationContext().getServletContext();
            return createTemplateInfo(path, sc.getResourceAsStream(path));
        } else {
            return createTemplateInfo(path, servletContext.getResourceAsStream(path));
        }
    }

}
