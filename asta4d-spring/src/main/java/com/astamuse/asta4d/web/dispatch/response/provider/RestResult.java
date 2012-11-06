package com.astamuse.asta4d.web.dispatch.response.provider;

public class RestResult {

    private int status;

    public RestResult(int status) {
        this.status = status;
    }

    public int getStatus() {
        return status;
    }

}
