package com.astamuse.asta4d.web.dispatch.response.provider;

import java.net.HttpURLConnection;

import com.astamuse.asta4d.web.dispatch.annotation.ContentProvider;
import com.astamuse.asta4d.web.dispatch.annotation.RequestHandlerResult;

public class RestResultProvider {

    @ContentProvider
    public RestResult response(@RequestHandlerResult RestResult result) {
        if (result == null) {
            return new RestResult(HttpURLConnection.HTTP_OK);
        } else {
            return result;
        }

    }
}
