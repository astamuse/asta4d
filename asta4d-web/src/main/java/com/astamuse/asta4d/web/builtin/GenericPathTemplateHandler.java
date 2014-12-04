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
package com.astamuse.asta4d.web.builtin;

import com.astamuse.asta4d.template.TemplateNotFoundException;
import com.astamuse.asta4d.web.WebApplicationContext;
import com.astamuse.asta4d.web.dispatch.mapping.UrlMappingRule;
import com.astamuse.asta4d.web.dispatch.request.RequestHandler;

public class GenericPathTemplateHandler extends AbstractGenericPathHandler {

    public GenericPathTemplateHandler() {
    }

    public GenericPathTemplateHandler(String basePath) {
        super(basePath);
    }

    @RequestHandler
    public Object handle(UrlMappingRule currentRule) {
        String path = super.convertPath(currentRule);
        if (path == null) {
            WebApplicationContext context = WebApplicationContext.getCurrentThreadWebApplicationContext();
            String url = context.getAccessURI();
            return new TemplateNotFoundException("Generically convert from path:" + url);
        } else {
            return path;
        }
    }
}
