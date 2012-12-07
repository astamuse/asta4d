/*
 * Copyright 2012 astamuse company,Ltd.
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

package com.astamuse.asta4d.web.dispatch.mapping;

import com.astamuse.asta4d.web.dispatch.response.writer.ContentWriter;

public class ResultDescriptor {

    private Class<?> resultTypeIdentifier = null;

    private Object resultInstanceIdentifier = null;

    private Object contentProvider = null;

    private ContentWriter writer = null;

    @SuppressWarnings("rawtypes")
    public ResultDescriptor(Object resultIdentifier, Object contentProvider, ContentWriter writer) {
        super();
        this.contentProvider = contentProvider;
        this.writer = writer;
        if (resultIdentifier instanceof Class) {
            resultTypeIdentifier = (Class) resultIdentifier;
        } else {
            resultInstanceIdentifier = resultIdentifier;
        }
    }

    public Class<?> getResultTypeIdentifier() {
        return resultTypeIdentifier;
    }

    public Object getResultInstanceIdentifier() {
        return resultInstanceIdentifier;
    }

    public Object getContentProvider() {
        return contentProvider;
    }

    public ContentWriter getWriter() {
        return writer;
    }

}
