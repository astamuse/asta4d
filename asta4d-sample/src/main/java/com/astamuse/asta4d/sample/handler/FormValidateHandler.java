package com.astamuse.asta4d.sample.handler;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.astamuse.asta4d.web.dispatch.request.RequestHandler;
import com.astamuse.asta4d.web.dispatch.response.provider.RedirectTargetProvider;

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
