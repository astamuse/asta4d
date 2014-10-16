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

package com.astamuse.asta4d.test.render;

import java.util.Arrays;
import java.util.Locale;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.astamuse.asta4d.Configuration;
import com.astamuse.asta4d.Context;
import com.astamuse.asta4d.render.GoThroughRenderer;
import com.astamuse.asta4d.render.Renderer;
import com.astamuse.asta4d.test.render.infra.BaseTest;
import com.astamuse.asta4d.test.render.infra.SimpleCase;
import com.astamuse.asta4d.util.i18n.I18nMessageHelper;
import com.astamuse.asta4d.util.i18n.I18nMessageHelperTypeAssistant;
import com.astamuse.asta4d.util.i18n.MappedValueI18nMessageHelper;
import com.astamuse.asta4d.util.i18n.OrderedValueI18nMessageHelper;
import com.astamuse.asta4d.util.i18n.formatter.ApacheStrSubstitutorFormatter;
import com.astamuse.asta4d.util.i18n.formatter.JDKMessageFormatFormatter;
import com.astamuse.asta4d.util.i18n.formatter.SymbolPlaceholderFormatter;
import com.astamuse.asta4d.util.i18n.pattern.JDKResourceBundleMessagePatternRetriever;

public class ExternalizeMessageTest extends BaseTest {

    public static class TestSnippet {
        public Renderer setWeatherreportInfo_NumberedParamKey() {
            Renderer renderer = new GoThroughRenderer();
            renderer.add("afd|msg", "p0", "Tomorrow");
            renderer.add("afd|msg", "p1", "sunny");
            return renderer;
        }

        public Renderer setWeatherreportInfo_NamedParamKey() {
            Renderer renderer = new GoThroughRenderer();
            renderer.add("afd|msg", "date", "Tomorrow");
            renderer.add("afd|msg", "weather", "sunny");
            return renderer;
        }
    }

    @BeforeClass
    public void setDefaultLocale() {
        Locale.setDefault(Locale.ROOT);
    }

    @Test
    public void externalizeMessage_DefaultMsg() throws Throwable {
        Context.getCurrentThreadContext().setCurrentLocale(Locale.US);
        setUpResourceBundleManager("symbol_placeholder_messages", new MappedValueI18nMessageHelper(new ApacheStrSubstitutorFormatter()));
        new SimpleCase("ExternalizeMessage_DefaultMsg.html", "ExternalizeMessage_DefaultMsg.html");
    }

    @Test
    public void externalizeMessage_SymbolPlaceholder_us() throws Throwable {
        Context.getCurrentThreadContext().setCurrentLocale(Locale.US);
        setUpResourceBundleManager("symbol_placeholder_messages", new OrderedValueI18nMessageHelper((new SymbolPlaceholderFormatter())));
        new SimpleCase("ExternalizeMessage_NumberedParamKey.html", "ExternalizeMessage_SymbolPlaceholder_us.html");
    }

    @Test
    public void externalizeMessage_SymbolPlaceholder_ja() throws Throwable {
        Context.getCurrentThreadContext().setCurrentLocale(Locale.JAPAN);
        setUpResourceBundleManager("symbol_placeholder_messages", new OrderedValueI18nMessageHelper((new SymbolPlaceholderFormatter())));
        new SimpleCase("ExternalizeMessage_NumberedParamKey.html", "ExternalizeMessage_SymbolPlaceholder_ja.html");
    }

    @Test
    public void externalizeMessage_JDKMessageFormat_us() throws Throwable {
        Context.getCurrentThreadContext().setCurrentLocale(Locale.US);
        setUpResourceBundleManager("number_placeholder_messages", new OrderedValueI18nMessageHelper(new JDKMessageFormatFormatter()));
        new SimpleCase("ExternalizeMessage_NumberedParamKey.html", "ExternalizeMessage_SymbolPlaceholder_us.html");
    }

    @Test
    public void externalizeMessage_JDKMessageFormat_ja() throws Throwable {
        Context.getCurrentThreadContext().setCurrentLocale(Locale.JAPAN);
        setUpResourceBundleManager("number_placeholder_messages", new OrderedValueI18nMessageHelper(new JDKMessageFormatFormatter()));
        new SimpleCase("ExternalizeMessage_NumberedParamKey.html", "ExternalizeMessage_SymbolPlaceholder_ja.html");
    }

    @Test
    public void externalizeMessage_NamedPlaceholder_us() throws Throwable {
        Context.getCurrentThreadContext().setCurrentLocale(Locale.US);
        setUpResourceBundleManager("named_placeholder_messages", new MappedValueI18nMessageHelper(new ApacheStrSubstitutorFormatter()));
        new SimpleCase("ExternalizeMessage_NamedParamKey.html", "ExternalizeMessage_SymbolPlaceholder_us.html");
    }

    @Test
    public void externalizeMessage_NamedPlaceholder_ja() throws Throwable {
        Context.getCurrentThreadContext().setCurrentLocale(Locale.JAPAN);
        setUpResourceBundleManager("named_placeholder_messages", new MappedValueI18nMessageHelper(new ApacheStrSubstitutorFormatter()));
        new SimpleCase("ExternalizeMessage_NamedParamKey.html", "ExternalizeMessage_SymbolPlaceholder_ja.html");
    }

    private static void setUpResourceBundleManager(String fileName, I18nMessageHelper helper) {
        Configuration configuration = Configuration.getConfiguration();
        configuration.setI18nMessageHelper(helper);
        JDKResourceBundleMessagePatternRetriever retriever = (JDKResourceBundleMessagePatternRetriever) configuration
                .getI18nMessageHelper().getMessagePatternRetriever();
        retriever.setResourceNames(Arrays.asList("com.astamuse.asta4d.test.render.messages." + fileName));

        // re initialize the I18nMessageTypeAssistant
        System.setProperty("I18nMessageHelperTypeAssistant.Test", "xxx");
        I18nMessageHelperTypeAssistant.reCreateInternalInstance();
    }
}
