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

import java.util.Date;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.testng.annotations.Test;

import com.astamuse.asta4d.render.Renderer;
import com.astamuse.asta4d.util.Java8TimeUtil;
import com.astamuse.asta4d.web.form.field.impl.DateRenderer;
import com.astamuse.asta4d.web.test.WebTestBase;
import com.astamuse.asta4d.web.test.form.FormRenderCase;

public class DateTest extends WebTestBase {
    public static class TestSnippet {
        public Renderer normalEditValue() {
            FieldRenderBuilder builder = FieldRenderBuilder.of(DateRenderer.class);
            builder.addValue("nullvalue", null);
            builder.addValue("emptyvalue", "");
            builder.addValue("datevalue", new Date(2014 - 1900, 10 - 1, 23, 12, 00, 00));
            builder.addValue("jodadatetimevalue", new DateTime(2014, 10, 23, 12, 00, 00));
            builder.addValue("jodalocaldatevalue", new LocalDate(2014, 10, 23));
            builder.addValue("java8instantvalue",
                    java.time.LocalDateTime.of(2014, 10, 23, 12, 00).toInstant(Java8TimeUtil.defaultZoneOffset()));
            builder.addValue("java8localdatetimevalue", java.time.LocalDateTime.of(2014, 10, 23, 12, 0, 0));
            builder.addValue("java8localdatevalue", java.time.LocalDate.of(2014, 10, 23));

            return builder.toRenderer(true);
        }

        public Renderer normalDisplayValue() {
            FieldRenderBuilder builder = FieldRenderBuilder.of(DateRenderer.class);
            builder.addValue("nullvalue", null);
            builder.addValue("emptyvalue", "");
            builder.addValue("datevalue", new Date(2014 - 1900, 10 - 1, 23, 12, 00, 00));
            builder.addValue("jodadatetimevalue", new DateTime(2014, 10, 23, 12, 00, 00));
            builder.addValue("jodalocaldatevalue", new LocalDate(2014, 10, 23));
            builder.addValue("java8instantvalue",
                    java.time.LocalDateTime.of(2014, 10, 23, 12, 00).toInstant(Java8TimeUtil.defaultZoneOffset()));
            builder.addValue("java8localdatetimevalue", java.time.LocalDateTime.of(2014, 10, 23, 12, 0, 0));
            builder.addValue("java8localdatevalue", java.time.LocalDate.of(2014, 10, 23));
            return builder.toRenderer(false);
        }
    }

    @Test
    public void test() throws Throwable {
        new FormRenderCase("/Date.html");
    }
}
