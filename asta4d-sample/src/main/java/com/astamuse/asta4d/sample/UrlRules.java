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
import com.astamuse.asta4d.sample.handler.form.CascadeFormHandler;
import com.astamuse.asta4d.sample.handler.form.MultiStepFormHandler;
import com.astamuse.asta4d.sample.handler.form.SingleInputFormHandler;
import com.astamuse.asta4d.sample.handler.form.SplittedFormHandler;
import com.astamuse.asta4d.web.builtin.StaticResourceHandler;
import com.astamuse.asta4d.web.dispatch.HttpMethod;
import com.astamuse.asta4d.web.dispatch.mapping.UrlMappingRuleInitializer;
import com.astamuse.asta4d.web.dispatch.mapping.ext.UrlMappingRuleHelper;
import com.astamuse.asta4d.web.dispatch.request.RequestHandler;
import com.astamuse.asta4d.web.form.flow.classical.MultiStepFormFlowHandler;

public class UrlRules implements UrlMappingRuleInitializer {

    @Override
    public void initUrlMappingRules(UrlMappingRuleHelper rules) {

        // global error handling

        // @ShowCode:showGlobal404RuleStart
        rules.addGlobalForward(PageNotFoundException.class, "/templates/errors/404.html", 404);
        // @ShowCode:showGlobal404RuleEnd

        // @ShowCode:showGlobalErrorRuleStart
        rules.addGlobalForward(Throwable.class, "/templates/errors/500.html", 500);
        // @ShowCode:showGlobalErrorRuleEnd

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
        rules.add("/var-injection/{name}/{age}", "/templates/variableinjection.html").priority(1);
        // @ShowCode:showVariableinjectionEnd
        
        rules.add("/attributevalues", "/templates/attributevalues.html");

        rules.add("/extend/{target}").handler(new Object(){
            @RequestHandler
            public String handle(String target){
                return "/templates/extend/"+target+".html";
            }
        });
        

        rules.add("/embed/main", "/templates/embed/main.html");

        rules.add("/ajax/getUserList").handler(GetUserListHandler.class).json();
        
        rules.add(PUT, "/ajax/addUser").handler(AddUserHandler.class).rest();
        
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
        
        // @ShowCode:showSingleInputStart
        rules.add((HttpMethod)null, "/form/singleInput")
             //specify the target template file of input page by constructor
             .handler(new SingleInputFormHandler("/templates/form/singleInput/edit.html"))
             //specify the exit target
             .redirect("/form?type=single-input");
        // @ShowCode:showSingleInputEnd
             
        // @ShowCode:showMultiStepStart
        rules.add((HttpMethod)null, "/form/multistep")
             //specify the base path of target template file by constructor
             .handler(new MultiStepFormHandler("/templates/form/multistep/"))
             //specify the exit target
             .redirect("/form?type=multi-step");
        // @ShowCode:showMultiStepEnd
        
        // @ShowCode:showCascadeStart
        rules.add((HttpMethod)null, "/form/cascade/add")
             //specify the base path of target template file by path var
             .var(MultiStepFormFlowHandler.VAR_TEMPLATE_BASE_PATH, "/templates/form/cascade/")
             .handler(CascadeFormHandler.Add.class)
             //specify the exit target
             .redirect("/form?type=cascade");

        rules.add((HttpMethod)null, "/form/cascade/edit")
             //specify the base path of target template file by path var
             .var(MultiStepFormFlowHandler.VAR_TEMPLATE_BASE_PATH, "/templates/form/cascade/")
             .handler(CascadeFormHandler.Edit.class)
             //specify the exit target
             .redirect("/form?type=cascade");
        // @ShowCode:showCascadeEnd
        
        // @ShowCode:showSplittedStart
        rules.add((HttpMethod)null, "/form/splitted/add")
             //specify the base path of target template file by path var
             .var(MultiStepFormFlowHandler.VAR_TEMPLATE_BASE_PATH, "/templates/form/splitted/")
             .handler(SplittedFormHandler.Add.class)
             //specify the exit target
             .redirect("/form?type=splitted");

        rules.add((HttpMethod)null, "/form/splitted/edit")
             //specify the base path of target template file by path var
             .var(MultiStepFormFlowHandler.VAR_TEMPLATE_BASE_PATH, "/templates/form/splitted/")
             .handler(SplittedFormHandler.Edit.class)
             //specify the exit target
             .redirect("/form?type=splitted");
        // @ShowCode:showSplittedEnd

        rules.add("/localize", "/templates/localize.html");
        
        rules.add("/error-sample/handler").handler(new Object(){
            @RequestHandler
            public void handle(){
                throw new RuntimeException("error in /error-sample/handler"); 
            }
        });
        
        rules.add("/error-sample/handler404").handler(new Object(){
            @RequestHandler
            public void handle(){
                throw new PageNotFoundException();
            }
        });
        
        rules.add("/error-sample/snippet", "/templates/snippet-error.html");
        
        //@formatter:on
    }
}
