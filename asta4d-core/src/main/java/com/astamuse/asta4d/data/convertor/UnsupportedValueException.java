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
package com.astamuse.asta4d.data.convertor;

import com.astamuse.asta4d.data.DataOperationException;
import com.astamuse.asta4d.data.DefaultDataTypeTransformer;

/**
 * 
 * A {@link DataValueConvertor} would throw this exception to show that it cannot convert the given value even the type is matched. The
 * {@link DefaultDataTypeTransformer} would ignore this exception and try the left convertors in the list.
 * 
 * @author e-ryu
 * 
 */
public class UnsupportedValueException extends DataOperationException {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public UnsupportedValueException() {
        super("");
    }

}
