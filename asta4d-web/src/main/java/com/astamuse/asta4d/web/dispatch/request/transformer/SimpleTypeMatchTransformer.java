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

package com.astamuse.asta4d.web.dispatch.request.transformer;

import com.astamuse.asta4d.web.dispatch.request.ResultTransformer;

public class SimpleTypeMatchTransformer implements ResultTransformer {

    private Class<?> resultTypeIdentifier = null;

    private Object resultInstanceIdentifier = null;

    private Object transformedResult;

    public SimpleTypeMatchTransformer(Object obj, Object transformedResult) {
        super();
        this.transformedResult = transformedResult;
        if (obj instanceof Class) {
            resultTypeIdentifier = (Class<?>) obj;
        } else {
            resultInstanceIdentifier = obj;
        }
    }

    public boolean isAsDefaultMatch() {
        return resultTypeIdentifier == null && resultInstanceIdentifier == null;
    }

    @Override
    public Object transformToContentProvider(Object result) {
        if (resultTypeIdentifier == null && resultInstanceIdentifier == null) {
            return this.transformedResult;
        } else if (result == null) {
            return null;
        } else if (resultTypeIdentifier != null) {
            if (resultTypeIdentifier.isAssignableFrom(result.getClass())) {
                return this.transformedResult;
            } else if (resultTypeIdentifier.equals(result.getClass())) {
                return this.transformedResult;
            }
        } else if (resultInstanceIdentifier != null) {
            if (resultInstanceIdentifier == result || resultInstanceIdentifier.equals(result)) {
                return this.transformedResult;
            }
        }
        return null;
    }

}
