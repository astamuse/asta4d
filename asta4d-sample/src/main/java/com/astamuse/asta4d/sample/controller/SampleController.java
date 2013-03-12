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

package com.astamuse.asta4d.sample.controller;

import static com.astamuse.asta4d.web.dispatch.HttpMethod.GET;
import static com.astamuse.asta4d.web.dispatch.HttpMethod.POST;
import static com.astamuse.asta4d.web.dispatch.HttpMethod.PUT;

import com.astamuse.asta4d.misc.spring.mvc.controller.GenericControllerBase;
import com.astamuse.asta4d.sample.forward.LoginFailure;
import com.astamuse.asta4d.sample.handler.AddUserHandler;
import com.astamuse.asta4d.sample.handler.EchoHandler;
import com.astamuse.asta4d.sample.handler.FormCompleteHandler;
import com.astamuse.asta4d.sample.handler.FormValidateHandler;
import com.astamuse.asta4d.sample.handler.GetUserListHandler;
import com.astamuse.asta4d.sample.handler.LoginHandler;
import com.astamuse.asta4d.web.dispatch.mapping.ext.UrlMappingRuleHelper;

public class SampleController extends GenericControllerBase {

    @Override
    protected void initUrlMappingRules(UrlMappingRuleHelper rules) {
        //@formatter:off
        rules.add(GET, "/")
             .redirect("/app/index");
        
        rules.add("/app/", "/templates/index.html");
        rules.add("/app/index", "/templates/index.html");

        rules.add("/app/snippet", "/templates/snippet.html");
        rules.add("/app/{name}/{age}", "/templates/variableinjection.html").priority(1);
        rules.add("/app/attributevalues", "/templates/attributevalues.html");

        rules.add("/app/extend/appendchild", "/templates/extend/appendchild.html");
        rules.add("/app/extend/insertchild", "/templates/extend/insertchild.html");
        rules.add("/app/extend/overridechild", "/templates/extend/overridechild.html");

        rules.add("/app/embed/main", "/templates/embed/main.html");

        rules.add("/app/ajax/getUserList").handler(GetUserListHandler.class).json();
        
        rules.add(PUT, "/app/ajax/addUser").handler(AddUserHandler.class).rest();
        
        rules.add("/app/", "/templates/index.html");

        rules.add("/app/handler")
             .handler(LoginHandler.class)
             .handler(EchoHandler.class)
             .forward(LoginFailure.class, "/templates/error.html")
             .forward("/templates/success.html");
        

        rules.add("/app/renderertypes", "/templates/renderertypes.html");
        rules.add("/app/passvariables", "/templates/passvariables.html");
        rules.add("/app/dynamicsnippet", "/templates/dynamicsnippet.html");

        rules.add("/app/contextdata", "/templates/contextdata.html");

        rules.add("/app/form/input", "/templates/form/input.html");
        rules.add(POST, "/app/form/confirm").handler(FormValidateHandler.class);
        rules.add(POST, "/app/form/complete").handler(FormCompleteHandler.class);

        rules.add("/app/localize", "/templates/localize.html");
        //@formatter:on
    }
}
