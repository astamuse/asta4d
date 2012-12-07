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

package com.astamuse.asta4d.format;

import java.text.MessageFormat;

import com.astamuse.asta4d.util.InvalidMessageException;

public class NumberPlaceholderFormatter extends ParamOrderDependentFormatter {

    @Override
    public String format(String pattern, Object... params) throws InvalidMessageException {
        try {
            return MessageFormat.format(pattern, params);
        } catch (IllegalArgumentException e) {
            throw new InvalidMessageException(e);
        }
    }
}
