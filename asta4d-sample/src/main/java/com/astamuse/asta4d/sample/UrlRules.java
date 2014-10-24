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

package com.astamuse.asta4d.sample;

import static com.astamuse.asta4d.web.dispatch.HttpMethod.GET;
import static com.astamuse.asta4d.web.dispatch.HttpMethod.PUT;

import com.astamuse.asta4d.sample.forward.LoginFailure;
import com.astamuse.asta4d.sample.handler.AddUserHandler;
import com.astamuse.asta4d.sample.handler.EchoHandler;
import com.astamuse.asta4d.sample.handler.GetUserListHandler;
import com.astamuse.asta4d.sample.handler.LoginHandler;
import com.astamuse.asta4d.sample.handler.form.CascadeEditHandler;
import com.astamuse.asta4d.sample.handler.form.MultiStepEditHandler;
import com.astamuse.asta4d.sample.handler.form.OneStepEditHandler;
import com.astamuse.asta4d.web.builtin.StaticResourceHandler;
import com.astamuse.asta4d.web.dispatch.HttpMethod;
import com.astamuse.asta4d.web.dispatch.mapping.UrlMappingRuleInitializer;
import com.astamuse.asta4d.web.dispatch.mapping.ext.UrlMappingRuleHelper;
import com.astamuse.asta4d.web.form.flow.classical.MultiStepFormFlowHandler;

public class UrlRules implements UrlMappingRuleInitializer {

    @Override
    public void initUrlMappingRules(UrlMappingRuleHelper rules) {
        //@formatter:off
        rules.add("/", "/templates/index.html");
        rules.add("/index", "/templates/index.html");
        
        rules.add(GET, "/redirect-to-index").redirect("p:/index");
        
        initSampleRules(rules);
        //@formatter:on
    }

    private void initSampleRules(UrlMappingRuleHelper rules) {
        //@formatter:off
        
        rules.add("/js/**/*").handler(new StaticResourceHandler());
        
        rules.add("/snippet", "/templates/snippet.html");
        
        // @ShowCode:showVariableinjectionStart
        rules.add("/{name}/{age}", "/templates/variableinjection.html").priority(1);
        // @ShowCode:showVariableinjectionEnd
        
        rules.add("/attributevalues", "/templates/attributevalues.html");

        rules.add("/extendendchild", "/templates/extendendchild.html");
        rules.add("/extend/insertchild", "/templates/extend/insertchild.html");
        rules.add("/extend/overridechild", "/templates/extend/overridechild.html");

        rules.add("/embed/main", "/templates/embed/main.html");

        rules.add("/ajax/getUserList").handler(GetUserListHandler.class).json();
        
        rules.add(PUT, "/ajax/addUser").handler(AddUserHandler.class).rest();
        
        rules.add("/", "/templates/index.html");

        // @ShowCode:showSuccessStart
        rules.add("/handler")
             .handler(LoginHandler.class)
             .handler(EchoHandler.class)
             .forward(LoginFailure.class, "/templates/error.html")
             .forward("/templates/success.html");
        // @ShowCode:showSuccessEnd
        

        rules.add("/renderertypes", "/templates/renderertypes.html");
        rules.add("/passvariables", "/templates/passvariables.html");
        rules.add("/dynamicsnippet", "/templates/dynamicsnippet.html");

        rules.add("/contextdata", "/templates/contextdata.html");

        
        rules.add("/form", "/templates/form/list.html");
        
        rules.add((HttpMethod)null, "/form/onestep")
             .handler(new OneStepEditHandler("/templates/form/onestep/edit.html"))
             .redirect("/form");
             

        rules.add((HttpMethod)null, "/form/multistep")
             .handler(new MultiStepEditHandler("/templates/form/multistep/"))
             .redirect("/form");
        
        rules.add((HttpMethod)null, "/form/cascade/add")
             .var(MultiStepFormFlowHandler.VAR_TEMPLATE_BASE_PATH, "/templates/form/cascade/")
             .handler(CascadeEditHandler.Add.class)
             .redirect("/form");

        rules.add((HttpMethod)null, "/form/cascade/edit")
             .var(MultiStepFormFlowHandler.VAR_TEMPLATE_BASE_PATH, "/templates/form/cascade/")
             .handler(CascadeEditHandler.Edit.class)
             .redirect("/form");
           
        rules.add("/localize", "/templates/localize.html");
        
        
        
        
        //@formatter:on
    }
}
