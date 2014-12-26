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
package com.astamuse.asta4d.data;

/**
 * The policy of how to handle the unmatched type on data conversion.
 * 
 * @author e-ryu
 * 
 */
public enum TypeUnMacthPolicy {

    /**
     * throw exception
     */
    EXCEPTION,

    /**
     * assign default value to target
     */
    DEFAULT_VALUE,

    /**
     * assign default value to target, also save the trace information in context
     */
    DEFAULT_VALUE_AND_TRACE
}
