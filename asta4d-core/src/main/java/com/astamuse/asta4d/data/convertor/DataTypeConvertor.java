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

package com.astamuse.asta4d.data.convertor;

import com.astamuse.asta4d.data.InjectUtil;

/**
 * 
 * This interface is used by {@link InjectUtil} to convert context data to the appropriate type automatically
 * 
 * @author e-ryu
 * 
 * @param <S>
 *            source type
 * @param <T>
 *            target type
 */
public interface DataTypeConvertor<S, T> {

    /**
     * convert a data from the original type to a certain type
     * 
     * @param obj
     *            the data wanted to be converted
     * @return converted result
     */
    public T convert(S obj);
}
