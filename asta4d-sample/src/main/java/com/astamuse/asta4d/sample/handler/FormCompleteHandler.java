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

package com.astamuse.asta4d.sample.handler;

import java.util.HashMap;
import java.util.Map;

import com.astamuse.asta4d.web.dispatch.request.RequestHandler;
import com.astamuse.asta4d.web.dispatch.response.provider.RedirectDescriptor;
import com.astamuse.asta4d.web.dispatch.response.provider.RedirectTargetProvider;

public class FormCompleteHandler {

    @RequestHandler
    public Object complete(String name, String age, String bloodtype, String submit, String cancel) {
        if (cancel != null) {
            Map<String, Object> flashScopeData = new HashMap<>();
            flashScopeData.put("name", name);
            flashScopeData.put("age", age);
            flashScopeData.put("bloodtype", bloodtype);
            return new RedirectTargetProvider(new RedirectDescriptor("/app/form/input", flashScopeData));
        }
        if (submit != null) {
            System.out.println("[FormCompleteHandler:complete]" + String.format("name=%s, age=%s, bloodtype=%s", name, age, bloodtype));
            return "/templates/form/complete.html";
        }
        throw new IllegalStateException();
    }
}
