package com.astamuse.asta4d.sample.handler;

import org.apache.commons.lang3.StringUtils;

import com.astamuse.asta4d.sample.forward.LoginFailure;
import com.astamuse.asta4d.web.dispatch.request.RequestHandler;

public class LoginHandler {

    @RequestHandler
    public LoginFailure doLogin(String flag) throws LoginFailure {
        if (StringUtils.isEmpty(flag)) {
            return null;
        }
        if ("error".equals(flag)) {
            throw new LoginFailure();
        }
        if (!Boolean.parseBoolean(flag)) {
            return new LoginFailure();
        }
        return null;
    }
}
