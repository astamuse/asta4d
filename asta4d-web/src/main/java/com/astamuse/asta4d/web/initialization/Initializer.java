package com.astamuse.asta4d.web.initialization;

import java.io.InputStream;

import com.astamuse.asta4d.web.WebApplicationConfiguration;

public interface Initializer {
    public void initliaze(InputStream input, WebApplicationConfiguration configuration) throws Exception;
}
