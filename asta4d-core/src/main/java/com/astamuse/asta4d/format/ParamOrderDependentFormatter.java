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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.astamuse.asta4d.extnode.ExtNodeConstants;

public abstract class ParamOrderDependentFormatter implements PlaceholderFormatter {

    @Override
    public String format(String pattern, Map<String, Object> paramMap) {
        Object[] params = retrieveNumberedParamKeyList(paramMap);
        return format(pattern, params);
    }

    public abstract String format(String pattern, Object... params);

    private static Object[] retrieveNumberedParamKeyList(Map<String, Object> paramMap) {
        List<Object> numberedParamNameList = new ArrayList<>();
        for (int index = 0; paramMap.containsKey(ExtNodeConstants.MSG_NODE_ATTR_PARAM_PREFIX + index); index++) {
            String key = ExtNodeConstants.MSG_NODE_ATTR_PARAM_PREFIX + index;
            Object value = paramMap.get(key);
            numberedParamNameList.add(value);
        }
        return numberedParamNameList.toArray();
    }
}
