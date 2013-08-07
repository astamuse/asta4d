package com.astamuse.asta4d.util.concurrent;

import java.util.concurrent.ExecutorService;

import com.astamuse.asta4d.Configuration;

public class SnippetExecutorServiceUtil {

    private final static ExecutorService snippetExecutorService;
    static {
        Configuration conf = Configuration.getConfiguration();
        snippetExecutorService = conf.getSnippetExecutorFactory().createExecutorService();
    }

    public final static ExecutorService getExecutorService() {
        return snippetExecutorService;
    }

}
