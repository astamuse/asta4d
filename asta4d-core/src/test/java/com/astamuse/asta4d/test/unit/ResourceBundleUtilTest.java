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

package com.astamuse.asta4d.test.unit;

import static org.testng.Assert.assertEquals;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.astamuse.asta4d.Context;
import com.astamuse.asta4d.test.render.infra.BaseTest;
import com.astamuse.asta4d.util.i18n.I18nMessageHelper;
import com.astamuse.asta4d.util.i18n.MappedValueI18nMessageHelper;
import com.astamuse.asta4d.util.i18n.OrderedValueI18nMessageHelper;
import com.astamuse.asta4d.util.i18n.formatter.ApacheStrSubstitutorFormatter;
import com.astamuse.asta4d.util.i18n.formatter.JDKMessageFormatFormatter;
import com.astamuse.asta4d.util.i18n.formatter.SymbolPlaceholderFormatter;
import com.astamuse.asta4d.util.i18n.pattern.CharsetResourceBundleFactory;
import com.astamuse.asta4d.util.i18n.pattern.JDKResourceBundleMessagePatternRetriever;
import com.astamuse.asta4d.util.i18n.pattern.LatinEscapingResourceBundleFactory;

@Test
public class ResourceBundleUtilTest extends BaseTest {

    @BeforeClass
    public void setDefaultLocale() {
        Locale.setDefault(Locale.ROOT);
    }

    @Test
    public void useSymbolFormatter() throws Exception {
        OrderedValueI18nMessageHelper helper = new OrderedValueI18nMessageHelper(new SymbolPlaceholderFormatter());
        setUp(helper, "symbol_placeholder_messages");
        assertEquals(helper.getMessage("weatherreport1", "Tomorrow", "sunny"), "Tomorrow's weather is sunny.");
    }

    @Test
    public void useSymbolFormatterJaJp() throws Exception {
        OrderedValueI18nMessageHelper helper = new OrderedValueI18nMessageHelper(new SymbolPlaceholderFormatter());
        setUp(helper, "symbol_placeholder_messages");
        assertEquals(helper.getMessage(Locale.JAPAN, "weatherreport1", "明日", "晴れ"), "明日の天気は晴れです。");
    }

    @Test
    public void useNumberFormatter() throws Exception {
        OrderedValueI18nMessageHelper helper = new OrderedValueI18nMessageHelper(new JDKMessageFormatFormatter());
        setUp(helper, "number_placeholder_messages");
        assertEquals(helper.getMessage("weatherreport1", "Tomorrow", "sunny"), "Tomorrow's weather is sunny.");
    }

    @Test
    public void useNumberFormatterJaJp() throws Exception {
        OrderedValueI18nMessageHelper helper = new OrderedValueI18nMessageHelper(new JDKMessageFormatFormatter());
        setUp(helper, "number_placeholder_messages");
        assertEquals(helper.getMessage(Locale.JAPAN, "weatherreport1", "明日", "晴れ"), "明日の天気は晴れです。");
    }

    @Test
    public void useNamedFormatter() throws Exception {

        MappedValueI18nMessageHelper helper = new MappedValueI18nMessageHelper(new ApacheStrSubstitutorFormatter());
        setUp(helper, "named_placeholder_messages");
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("date", "Tomorrow");
        params.put("weather", "sunny");
        assertEquals(helper.getMessage("weatherreport1", params), "Tomorrow's weather is sunny.");
    }

    @Test
    public void useNamedFormatterWithSplittedMessagePattern() throws Exception {

        MappedValueI18nMessageHelper helper = new MappedValueI18nMessageHelper(new ApacheStrSubstitutorFormatter());
        setUp(helper, "named_placeholder_messages");
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("date", "Tomorrow");
        params.put("weather", "sunny");
        assertEquals(helper.getMessage("weatherreport-split", params), "Tomorrow's weather is sunny.");
    }

    @Test
    public void useNamedFormatterJaJp() throws Exception {

        MappedValueI18nMessageHelper helper = new MappedValueI18nMessageHelper(new ApacheStrSubstitutorFormatter());
        setUp(helper, "named_placeholder_messages");
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("date", "明日");
        params.put("weather", "晴れ");
        assertEquals(helper.getMessage(Locale.JAPAN, "weatherreport1", params), "明日の天気は晴れです。");
    }

    @Test
    public void utf8MessageFileJaJp() throws Exception {
        MappedValueI18nMessageHelper helper = new MappedValueI18nMessageHelper(new ApacheStrSubstitutorFormatter());
        setUp(helper, "utf_8_messages");
        // reset to utf-8
        ((JDKResourceBundleMessagePatternRetriever) helper.getMessagePatternRetriever())
                .setResourceBundleFactory(new CharsetResourceBundleFactory("UTF-8"));

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("date", "明日");
        params.put("weather", "晴れ");
        assertEquals(helper.getMessage(Locale.JAPAN, "weatherreport", params), "明日の天気は晴れです。");
    }

    private void setUp(I18nMessageHelper helper, String fileName) {
        Context context = Context.getCurrentThreadContext();
        context.setCurrentLocale(Locale.US);
        JDKResourceBundleMessagePatternRetriever retriever = (JDKResourceBundleMessagePatternRetriever) helper.getMessagePatternRetriever();
        retriever.setResourceNames(Arrays.asList("com.astamuse.asta4d.test.render.messages." + fileName));
        retriever.setResourceBundleFactory(new LatinEscapingResourceBundleFactory());
    }
}
