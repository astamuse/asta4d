package com.astamuse.asta4d.sample.handler;

import com.astamuse.asta4d.web.dispatch.annotation.RequestHandler;

public class EchoHandler {

    @RequestHandler
    public void echo(String value) {
        if (value == null || value.isEmpty()) {
            value = "hello!";
        }
        System.out.println("[EchoHandler:echo]" + value);
    }
}
