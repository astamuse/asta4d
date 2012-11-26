package com.astamuse.asta4d.sample.handler;

import org.apache.commons.lang3.StringUtils;

import com.astamuse.asta4d.web.dispatch.request.RequestHandler;

public class EchoHandler {

    @RequestHandler
    public void echo(String value) {
        if (StringUtils.isEmpty(value)) {
            value = "hello!";
        }
        System.out.println("[EchoHandler:echo]" + value);
    }
}
