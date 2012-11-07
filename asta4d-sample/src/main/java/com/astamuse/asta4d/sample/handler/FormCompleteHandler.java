package com.astamuse.asta4d.sample.handler;

import java.util.HashMap;
import java.util.Map;

import com.astamuse.asta4d.web.dispatch.request.RequestHandler;
import com.astamuse.asta4d.web.dispatch.response.provider.RedirectTargetProvider;

public class FormCompleteHandler {

    @RequestHandler
    public Object complete(String name, String age, String bloodtype, String submit, String cancel) {
        if (cancel != null) {
            Map<String, Object> flashScopeData = new HashMap<>();
            flashScopeData.put("name", name);
            flashScopeData.put("age", age);
            flashScopeData.put("bloodtype", bloodtype);
            return new RedirectTargetProvider("/app/form/input", flashScopeData);
        }
        if (submit != null) {
            System.out.println("[FormCompleteHandler:complete]" + String.format("name=%s, age=%s, bloodtype=%s", name, age, bloodtype));
            return "/templates/form/complete.html";
        }
        throw new IllegalStateException();
    }
}
