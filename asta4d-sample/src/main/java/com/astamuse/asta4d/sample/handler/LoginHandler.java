package com.astamuse.asta4d.sample.handler;

import org.apache.commons.lang3.StringUtils;

import com.astamuse.asta4d.web.dispatch.annotation.RequestHandler;

public class LoginHandler {

    @RequestHandler
    public String doLogin(String flag) {
        if (!login(flag)) {
            return "/templates/error.html";
        }
        return null;
    }

    // Actually, do login processing.
    private boolean login(String flag) {
        boolean success;
        if (StringUtils.isEmpty(flag)) {
            success = true;
        } else {
            success = Boolean.parseBoolean(flag);
        }
        System.out.println("[LoginHandler:login]" + success);
        return success;
    }
}
