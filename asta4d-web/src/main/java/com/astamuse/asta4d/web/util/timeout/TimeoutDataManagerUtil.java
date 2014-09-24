package com.astamuse.asta4d.web.util.timeout;

import com.astamuse.asta4d.web.WebApplicationConfiguration;

public class TimeoutDataManagerUtil {
    private static TimeoutDataManager manager;
    static {
        WebApplicationConfiguration conf = WebApplicationConfiguration.getWebApplicationConfiguration();
        manager = conf.getTimeoutDataManagerFactory().create();
    }

    public static TimeoutDataManager getManager() {
        return manager;
    }
}
