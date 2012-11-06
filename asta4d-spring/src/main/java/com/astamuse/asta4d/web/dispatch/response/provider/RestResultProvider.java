package com.astamuse.asta4d.web.dispatch.response.provider;

import com.astamuse.asta4d.web.dispatch.annotation.ContentProvider;
import com.astamuse.asta4d.web.dispatch.annotation.RequestHandlerResult;
import com.astamuse.asta4d.web.dispatch.response.RestResultWriter;

public class RestResultProvider {

    @ContentProvider(writer = RestResultWriter.class)
    public RestResult response(@RequestHandlerResult RestResult result) {
        return result;
    }
}
