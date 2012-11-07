package com.astamuse.asta4d.sample.handler;

import org.apache.commons.lang3.StringUtils;

import com.astamuse.asta4d.sample.forward.LoginFailureDescriptor;
import com.astamuse.asta4d.web.dispatch.request.RequestHandler;

public class LoginHandler {

    @RequestHandler
    public LoginFailureDescriptor doLogin(String flag) throws LoginFailureDescriptor {
        if (StringUtils.isEmpty(flag)) {
            return null;
        }
        if ("error".equals(flag)) {
            throw new LoginFailureDescriptor();
        }
        if (!Boolean.parseBoolean(flag)) {
            return new LoginFailureDescriptor();
        }
        return null;
    }
}
