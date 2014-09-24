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

package com.astamuse.asta4d.test.render.infra;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.testng.Assert;

import com.astamuse.asta4d.Page;

public class SimpleCase {

    public SimpleCase(String templateFileName) {
        this(templateFileName, templateFileName);
    }

    public SimpleCase(String templateFileName, String confirmFileName) {
        String ostr = null;
        String cstr = null;
        try {
            Page originPage = Page.buildFromPath(retrieveTempateFielParentPath() + templateFileName);
            ostr = revert2comparableString(originPage);

            cstr = revert2comparableString(retrieveConfirmFielParentPath() + confirmFileName);

            Assert.assertEquals(ostr, cstr);

        } catch (Throwable t) {
            output(templateFileName + ":rendering result", ostr);
            output(confirmFileName + ":expected result", cstr);
            throw new RuntimeException("verify failed", t);
        }
    }

    protected String retrieveTempateFielParentPath() {
        return "/com/astamuse/asta4d/test/render/templates/";
    }

    protected String retrieveConfirmFielParentPath() {
        return "/com/astamuse/asta4d/test/render/confirms/";
    }

    private String revert2comparableString(Page page) {
        return page.output();
    }

    private String revert2comparableString(String path) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(SimpleCase.class.getResourceAsStream(path), "UTF-8"))) {
            StringBuilder sb = new StringBuilder();
            char[] b = new char[1024];
            int line;
            while (0 <= (line = reader.read(b))) {
                sb.append(b, 0, line);
            }
            return sb.toString();
        }
    }

    private void output(String title, String page) {
        System.out.println(String.format("===============%s===================", title));
        if (page == null) {
            System.out.println("##null##");
        } else {
            System.out.println(page);
        }
        System.out.println("===============================================================");
    }

}
