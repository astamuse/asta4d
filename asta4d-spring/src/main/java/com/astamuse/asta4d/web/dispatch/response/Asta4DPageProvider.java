package com.astamuse.asta4d.web.dispatch.response;

import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpServletResponse;

import com.astamuse.asta4d.Context;
import com.astamuse.asta4d.template.TemplateException;
import com.astamuse.asta4d.util.LocalizeUtil;
import com.astamuse.asta4d.web.WebPage;

public class Asta4DPageProvider implements ContentProvider {

    private final static Map<String, WebPage> WebPageCache = new ConcurrentHashMap<>();

    private String path;

    private WebPage page = null;

    public Asta4DPageProvider(String path) throws TemplateException {
        this.path = path;
        Context context = Context.getCurrentThreadContext();
        Locale locale = context.getCurrentLocale();
        String cacheKey = LocalizeUtil.createLocalizedKey(path, locale);

        // TODO it should be better if there is a global helping cache
        // implementation
        WebPage cachedPage = context.getConfiguration().isCacheEnable() ? WebPageCache.get(cacheKey) : null;
        // It is OK if the page is created in multiple times
        if (cachedPage == null) {
            cachedPage = new WebPage(path);
            WebPageCache.put(cacheKey, cachedPage);
        }
        this.page = cachedPage;
    }

    public String getPath() {
        return path;
    }

    @Override
    public void produce(HttpServletResponse response) throws Exception {
        response.setContentType(page.getContentType());
        page.output(response.getOutputStream());
    }

}
