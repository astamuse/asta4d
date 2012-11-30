package com.astamuse.asta4d.sample.handler;

import java.util.Arrays;
import java.util.List;

import com.astamuse.asta4d.web.dispatch.request.RequestHandler;

public class GetUserListHandler {

    @RequestHandler
    public List<String> queryUserList() {
        return Arrays.asList("otani", "ryu", "mizuhara");
    }
}
