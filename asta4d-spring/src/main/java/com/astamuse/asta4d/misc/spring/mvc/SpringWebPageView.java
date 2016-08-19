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

import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.View;

import com.astamuse.asta4d.Context;
import com.astamuse.asta4d.template.TemplateException;
import com.astamuse.asta4d.web.WebApplicationContext;
import com.astamuse.asta4d.web.dispatch.mapping.UrlMappingRule;
import com.astamuse.asta4d.web.dispatch.response.provider.Asta4DPageProvider;

public class SpringWebPageView implements View {

    public static final String BODY_ONLY_FLAG = "BODY_ONLY#" + SpringWebPageView.class;

    private static final UrlMappingRule NormalPageRule;

    private static final UrlMappingRule BodyOnlyRule;

    static {
        NormalPageRule = new UrlMappingRule().asUnmodifiable();
        {
            UrlMappingRule rule = new UrlMappingRule();
            rule.getAttributeList().add(Asta4DPageProvider.AttrBodyOnly);
            BodyOnlyRule = rule.asUnmodifiable();
        }
    }

    private Asta4DPageProvider templateProvider;

    public SpringWebPageView(Asta4DPageProvider templateProvider) throws TemplateException {
        super();
        this.templateProvider = templateProvider;
    }

    @Override
    public String getContentType() {
        return "";
    }

    @Override
    public void render(Map<String, ?> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
        WebApplicationContext context = Context.getCurrentThreadContext();
        for (Entry<String, ?> entry : model.entrySet()) {
            context.setData(entry.getKey(), entry.getValue());
        }
        UrlMappingRule dummyRule;
        if (context.getData(BODY_ONLY_FLAG) != null) {
            dummyRule = BodyOnlyRule;
        } else {
            dummyRule = NormalPageRule;
        }
        templateProvider.produce(dummyRule, response);
    }

    public static void setCurrentRequestAsBodyOnly() {
        WebApplicationContext context = Context.getCurrentThreadContext();
        context.setData(BODY_ONLY_FLAG, "bodyonly");// the value is not matter
    }

}
