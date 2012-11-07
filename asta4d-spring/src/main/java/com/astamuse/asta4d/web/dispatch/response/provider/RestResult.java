package com.astamuse.asta4d.web.dispatch.response.provider;

public class RestResult extends HeaderInfoHoldingContent {

    public RestResult(int status) {
        super(status, null);
    }

}
