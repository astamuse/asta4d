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
package com.astamuse.asta4d.web.dispatch.request;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class MultiResultHolder {

    private List<Object> resultList;

    public MultiResultHolder() {
        resultList = new LinkedList<>();
    }

    public void addResult(Object result) {
        resultList.add(result);
    }

    public List<Object> getResultList() {
        return Collections.unmodifiableList(resultList);
    }
}
