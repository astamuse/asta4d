/*
 * Copyright 2014 astamuse company,Ltd.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */
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
