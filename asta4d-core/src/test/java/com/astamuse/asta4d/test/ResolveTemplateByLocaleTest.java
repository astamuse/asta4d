package com.astamuse.asta4d.test;

import java.util.Locale;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

import com.astamuse.asta4d.Context;
import com.astamuse.asta4d.test.infra.BaseTest;
import com.astamuse.asta4d.test.infra.SimpleCase;

public class ResolveTemplateByLocaleTest extends BaseTest {

    @AfterMethod
    public void clearCurrentLocale() {
        // TODO When we work out multithreaded rendering, this code have to
        // readjust.
        Context.getCurrentThreadContext().setCurrentLocale(null);
    }

    @Test
    public void currentLocaleJaJP() {
        Context.getCurrentThreadContext().setCurrentLocale(Locale.JAPAN);
        new SimpleCase("ResolveTemplateByLocale.html", "ResolveTemplateByLocale_ja_JP.html");
    }

    @Test
    public void currentLocaleJa() {
        Context.getCurrentThreadContext().setCurrentLocale(Locale.JAPANESE);
        new SimpleCase("ResolveTemplateByLocale.html", "ResolveTemplateByLocale.html");
    }

    @Test
    public void currentLocaleEnUS() {
        Context.getCurrentThreadContext().setCurrentLocale(Locale.US);
        new SimpleCase("ResolveTemplateByLocale.html", "ResolveTemplateByLocale_en.html");
    }

    @Test
    public void currentLocaleEnGB() {
        Context.getCurrentThreadContext().setCurrentLocale(Locale.UK);
        new SimpleCase("ResolveTemplateByLocale.html", "ResolveTemplateByLocale_en.html");
    }

    @Test
    public void currentLocaleDeDE() {
        Context.getCurrentThreadContext().setCurrentLocale(Locale.GERMANY);
        new SimpleCase("ResolveTemplateByLocale.html", "ResolveTemplateByLocale.html");
    }
}
