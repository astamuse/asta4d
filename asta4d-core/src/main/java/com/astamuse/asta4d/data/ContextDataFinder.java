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

package com.astamuse.asta4d.data;

import com.astamuse.asta4d.Context;

/**
 * This interface declares how to find a certain data in Context. Because there
 * are customized scopes, so an implementation can customize the search logic
 * against special scope.
 * 
 * @author e-ryu
 * 
 */
public interface ContextDataFinder {

    /**
     * find data from given context by certain logic
     * 
     * @param context
     *            {@link Context}
     * @param scope
     *            scope
     * @param name
     *            data saved key
     * @param type
     *            data type
     * @return the data saved in context or null if not found
     * @throws DataOperationException
     */
    public Object findDataInContext(Context context, String scope, String name, Class<?> type) throws DataOperationException;
}
