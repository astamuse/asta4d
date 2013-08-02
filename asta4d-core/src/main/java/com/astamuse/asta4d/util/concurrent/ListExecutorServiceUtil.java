package com.astamuse.asta4d.util.concurrent;

import java.util.concurrent.ExecutorService;

import com.astamuse.asta4d.Configuration;

public class ListExecutorServiceUtil {

    private final static ExecutorService listExecutorService;
    static {
        Configuration conf = Configuration.getConfiguration();
        listExecutorService = conf.getListExecutorFactory().createExecutorService();
    }

    public final static ExecutorService getExecutorService() {
        return listExecutorService;
    }

}
