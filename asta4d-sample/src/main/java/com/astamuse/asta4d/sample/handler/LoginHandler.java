package com.astamuse.asta4d.sample.handler;

import org.apache.commons.lang3.StringUtils;

import com.astamuse.asta4d.sample.forward.LoginFailureDescriptor;
import com.astamuse.asta4d.web.dispatch.request.RequestHandler;
import com.astamuse.asta4d.web.dispatch.response.forward.ForwardDescriptor;
import com.astamuse.asta4d.web.dispatch.response.forward.ForwardableException;

public class LoginHandler {

    @RequestHandler
    public ForwardDescriptor doLogin(String flag) {
        if (StringUtils.isEmpty(flag)) {
            return null;
        }
        if ("error".equals(flag)) {
            throw new ForwardableException(new LoginFailureDescriptor(), new IllegalArgumentException());
        }
        if (!Boolean.parseBoolean(flag)) {
            return new LoginFailureDescriptor();
        }
        return null;
    }
}
