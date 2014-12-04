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
package com.astamuse.asta4d.web.test.form;

import com.astamuse.asta4d.test.render.infra.SimpleCase;

public class FormRenderCase extends SimpleCase {

    public FormRenderCase(String templateFileName, String confirmFileName) throws Throwable {
        super(templateFileName, confirmFileName);
    }

    public FormRenderCase(String templateFileName) throws Throwable {
        super(templateFileName);
    }

    @Override
    protected String retrieveTempateFielParentPath() {
        return "/com/astamuse/asta4d/web/test/form/templates/";
    }

    @Override
    protected String retrieveConfirmFielParentPath() {
        return "/com/astamuse/asta4d/web/test/form/confirms/";
    }
}
