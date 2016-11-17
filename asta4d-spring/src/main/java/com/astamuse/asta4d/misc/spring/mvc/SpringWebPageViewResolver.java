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

package com.astamuse.asta4d.misc.spring.mvc;

import java.util.Locale;

import org.springframework.web.servlet.View;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.view.RedirectView;

import com.astamuse.asta4d.Configuration;
import com.astamuse.asta4d.template.Template;
import com.astamuse.asta4d.template.TemplateNotFoundException;
import com.astamuse.asta4d.template.TemplateResolver;

public class SpringWebPageViewResolver implements ViewResolver {

    private boolean exceptionOnTemplateNotFound = true;
    private String prefix = "";
    private String suffix = "";

    @Override
    public View resolveViewName(String viewName, Locale locale) throws Exception {
        try {
            // TODO we should follow the spring mvc default implementation of UrlBasedViewResolver
            String redirectPrefix = "redirect:";
            if (viewName.startsWith(redirectPrefix)) {
                String redirect = viewName.substring(redirectPrefix.length());
                return new RedirectView(redirect);
            }

            String path = prefix + viewName + suffix;
            Configuration conf = Configuration.getConfiguration();
            TemplateResolver templateResolver = conf.getTemplateResolver();
            Template template = templateResolver.findTemplate(path);
            return new SpringWebPageView(template);
        } catch (TemplateNotFoundException e) {
            if (exceptionOnTemplateNotFound) {
                throw e;
            } else {
                return null;
            }
        }
    }

    public void setExceptionOnTemplateNotFound(boolean exceptionOnTemplateNotFound) {
        this.exceptionOnTemplateNotFound = exceptionOnTemplateNotFound;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

}
