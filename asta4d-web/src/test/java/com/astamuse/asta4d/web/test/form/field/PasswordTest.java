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
package com.astamuse.asta4d.web.test.form.field;

import org.testng.annotations.Test;

import com.astamuse.asta4d.render.Renderer;
import com.astamuse.asta4d.web.form.field.impl.PasswordRenderer;
import com.astamuse.asta4d.web.test.WebTestBase;
import com.astamuse.asta4d.web.test.form.FormRenderCase;

public class PasswordTest extends WebTestBase {
    public static class TestSnippet {
        public Renderer normalEditValue() {
            FieldRenderBuilder builder = FieldRenderBuilder.of(PasswordRenderer.class);
            builder.addValue("nullvalue", null);
            builder.addValue("emptyvalue", "");
            builder.addValue("xvalue", "x");
            return builder.toRenderer(true);
        }

        public Renderer normalDisplayValue() {
            FieldRenderBuilder builder = FieldRenderBuilder.of(PasswordRenderer.class);
            builder.addValue("nullvalue", null);
            builder.addValue("emptyvalue", "");
            builder.addValue("xvalue", "x");
            return builder.toRenderer(false);
        }
    }

    @Test
    public void test() throws Throwable {
        new FormRenderCase("/Password.html");
    }
}
