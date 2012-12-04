package com.astamuse.asta4d.test.render;

import java.util.Locale;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.astamuse.asta4d.Configuration;
import com.astamuse.asta4d.Context;
import com.astamuse.asta4d.format.NamedPlaceholderFormatter;
import com.astamuse.asta4d.format.NumberPlaceholderFormatter;
import com.astamuse.asta4d.format.PlaceholderFormatter;
import com.astamuse.asta4d.format.SymbolPlaceholderFormatter;
import com.astamuse.asta4d.render.GoThroughRenderer;
import com.astamuse.asta4d.render.Renderer;
import com.astamuse.asta4d.test.render.infra.BaseTest;
import com.astamuse.asta4d.test.render.infra.SimpleCase;

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
    public void externalizeMessage_SymbolPlaceholder_us() {
        Context.getCurrentThreadContext().setCurrentLocale(Locale.US);
        setUpResourceBundleManager("symbol_placeholder_messages", new SymbolPlaceholderFormatter());
        new SimpleCase("ExternalizeMessage_NumberedParamKey.html", "ExternalizeMessage_SymbolPlaceholder_us.html");
    }

    @Test
    public void externalizeMessage_SymbolPlaceholder_ja() {
        Context.getCurrentThreadContext().setCurrentLocale(Locale.JAPAN);
        setUpResourceBundleManager("symbol_placeholder_messages", new SymbolPlaceholderFormatter());
        new SimpleCase("ExternalizeMessage_NumberedParamKey.html", "ExternalizeMessage_SymbolPlaceholder_ja.html");
    }

    @Test
    public void externalizeMessage_NumberPlaceholder_us() {
        Context.getCurrentThreadContext().setCurrentLocale(Locale.US);
        setUpResourceBundleManager("number_placeholder_messages", new NumberPlaceholderFormatter());
        new SimpleCase("ExternalizeMessage_NumberedParamKey.html", "ExternalizeMessage_SymbolPlaceholder_us.html");
    }

    @Test
    public void externalizeMessage_NumberPlaceholder_ja() {
        Context.getCurrentThreadContext().setCurrentLocale(Locale.JAPAN);
        setUpResourceBundleManager("number_placeholder_messages", new NumberPlaceholderFormatter());
        new SimpleCase("ExternalizeMessage_NumberedParamKey.html", "ExternalizeMessage_SymbolPlaceholder_ja.html");
    }

    @Test
    public void externalizeMessage_NamedPlaceholder_us() {
        Context.getCurrentThreadContext().setCurrentLocale(Locale.US);
        setUpResourceBundleManager("named_placeholder_messages", new NamedPlaceholderFormatter());
        new SimpleCase("ExternalizeMessage_NamedParamKey.html", "ExternalizeMessage_SymbolPlaceholder_us.html");
    }

    @Test
    public void externalizeMessage_NamedPlaceholder_ja() {
        Context.getCurrentThreadContext().setCurrentLocale(Locale.JAPAN);
        setUpResourceBundleManager("named_placeholder_messages", new NamedPlaceholderFormatter());
        new SimpleCase("ExternalizeMessage_NamedParamKey.html", "ExternalizeMessage_SymbolPlaceholder_ja.html");
    }

    private static void setUpResourceBundleManager(String fileName, PlaceholderFormatter formatter) {
        Configuration configuration = Context.getCurrentThreadContext().getConfiguration();
        configuration.setPlaceholderFormatter(formatter);
        configuration.setResourceNames("com.astamuse.asta4d.test.render.messages." + fileName);
    }
}
