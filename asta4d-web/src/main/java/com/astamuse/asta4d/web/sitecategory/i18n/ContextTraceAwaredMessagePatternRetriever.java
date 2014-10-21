package com.astamuse.asta4d.web.sitecategory.i18n;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import javax.servlet.ServletContext;

import com.astamuse.asta4d.Context;
import com.astamuse.asta4d.render.RenderUtil;
import com.astamuse.asta4d.util.i18n.pattern.JDKResourceBundleMessagePatternRetriever;
import com.astamuse.asta4d.web.WebApplicationContext;
import com.astamuse.asta4d.web.dispatch.DefaultRequestHandlerInvoker;

public class ContextTraceAwaredMessagePatternRetriever extends JDKResourceBundleMessagePatternRetriever {

    @Override
    public String retrieve(Locale locale, String key) {
        Context context = Context.getCurrentThreadContext();

        String currentTemplate = context.getData(RenderUtil.TRACE_VAR_TEMPLATE_PATH);
        if (currentTemplate != null) {
            ResourceBundle rb = getResourceBundleForTemplate(convertBaseName(currentTemplate), locale);
            return retrieveResourceFromBundle(rb, key);
        }

        Object handler = context.getData(DefaultRequestHandlerInvoker.TRACE_VAR_CURRENT_HANDLER);
        if (handler != null) {
            if (handler instanceof ClassLocationBindedMessageResource) {
                ResourceBundle rb = getResourceBundleForClass(handler.getClass().getName(), locale);
                return retrieveResourceFromBundle(rb, key);
            }
        }

        // final fallback
        return super.retrieve(locale, key);
    }

    protected String convertBaseName(String path) {
        int dotIdx = path.lastIndexOf('.');
        if (dotIdx >= 0) {
            return path.substring(0, dotIdx);
        } else {
            return path;
        }
    }

    protected ResourceBundle getResourceBundleForTemplate(String baseName, Locale locale) {
        return ResourceBundle.getBundle(baseName, locale == null ? Locale.getDefault() : locale, new ResourceBundle.Control() {
            @Override
            public ResourceBundle newBundle(String baseName, Locale locale, String format, ClassLoader loader, boolean reload)
                    throws IllegalAccessException, InstantiationException, IOException {
                String bundleName = toBundleName(baseName, locale);
                String resourceName = toResourceName(bundleName, "properties");

                WebApplicationContext context = WebApplicationContext.getCurrentThreadWebApplicationContext();

                ServletContext sc = context == null ? null : context.getServletContext();

                InputStream stream = null;
                if (sc == null) {
                    // for test purpose, we will treat it from class path when servlet context is null
                    if (resourceName.startsWith("/")) {
                        stream = loader.getResourceAsStream(resourceName.substring(1));
                    } else {
                        stream = loader.getResourceAsStream(resourceName);
                    }
                } else {
                    sc.getResourceAsStream(resourceName);
                }

                ResourceBundle bundle = null;
                if (stream != null) {
                    try {
                        bundle = new PropertyResourceBundle(new InputStreamReader(stream, StandardCharsets.UTF_8));
                    } finally {
                        stream.close();
                    }
                }

                return bundle;
            }

        });
    }

    protected ResourceBundle getResourceBundleForClass(String baseName, Locale locale) {
        return super.getResourceBundle(baseName, locale);
    }
}
