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
package com.astamuse.asta4d.sample.handler.form;

import com.astamuse.asta4d.data.annotation.ContextDataSet;
import com.astamuse.asta4d.web.annotation.QueryParam;

@ContextDataSet
public class SubPersonFormDataHolder {

    @QueryParam
    private String[] name;

    @QueryParam
    private String[] age;

    public String[] getName() {
        return name;
    }

    public void setName(String[] name) {
        this.name = name;
    }

    public String[] getAge() {
        return age;
    }

    public void setAge(String[] age) {
        this.age = age;
    }

}
