package com.astamuse.asta4d.sample.handler;

import com.astamuse.asta4d.web.dispatch.request.RequestHandler;
import com.astamuse.asta4d.web.dispatch.response.provider.HeaderInfo;

public class AddUserHandler {

    @RequestHandler
    public HeaderInfo doAdd(String newUserName) {
        // some logic that should add a new user by the given name
        // ...
        return new HeaderInfo(200);
    }
}
