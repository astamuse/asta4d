package com.astamuse.asta4d.sample;

import com.astamuse.asta4d.web.WebApplicationConfiguration;
import com.astamuse.asta4d.web.util.message.DefaultMessageRenderingHelper;

public class MessageRenderingHelperFactory {

    private static final DefaultMessageRenderingHelper helper = (DefaultMessageRenderingHelper) WebApplicationConfiguration
            .getWebApplicationConfiguration().getMessageRenderingHelper();

    public static final DefaultMessageRenderingHelper getHelper() {
        return helper;
    }
}
