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

import org.apache.commons.lang3.StringUtils;

import com.astamuse.asta4d.web.dispatch.request.RequestHandler;
import com.astamuse.asta4d.web.dispatch.response.provider.RedirectTargetProvider;

//@ShowCode:showFormValidateHandlerStart
public class FormValidateHandler {

    @RequestHandler
    public Object complete(String name, String age, String bloodtype) {
        String nameErrMsg = null;
        String ageErrMsg = null;
        if (StringUtils.isEmpty(name)) {
            nameErrMsg = "name is required.";
        }
        if (StringUtils.isEmpty(age)) {
            ageErrMsg = "age is required.";
        } else {
            try {
                Integer.valueOf(age);
            } catch (NumberFormatException e) {
                ageErrMsg = "age must be numeric value.";
            }
        }
        if (nameErrMsg == null && ageErrMsg == null) {
            return "/templates/form/confirm.html";
        } else {
            Map<String, Object> flashScopeData = new HashMap<>();
            flashScopeData.put("name", name);
            flashScopeData.put("age", age);
            flashScopeData.put("bloodtype", bloodtype);
            flashScopeData.put("nameErrMsg", nameErrMsg);
            flashScopeData.put("ageErrMsg", ageErrMsg);
            return new RedirectTargetProvider("/app/form/input", flashScopeData);
        }
    }
}
// @ShowCode:showFormValidateHandlerEnd
