package com.astamuse.asta4d.template;

import java.io.InputStream;
import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.astamuse.asta4d.Configuration;
import com.astamuse.asta4d.Context;
import com.astamuse.asta4d.util.LocalizeUtil;
import com.astamuse.asta4d.util.MultiSearchPathResourceLoader;

//TODO internationalization
public abstract class TemplateResolver extends MultiSearchPathResourceLoader<InputStream> {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private final static ConcurrentHashMap<String, Template> templateMap = new ConcurrentHashMap<String, Template>();

    public Template findTemplate(String path) throws TemplateException {
        try {
            Configuration conf = Context.getCurrentThreadContext().getConfiguration();
            Locale locale = Context.getCurrentThreadContext().getCurrentLocale();
            String cacheKey = LocalizeUtil.createLocalizedKey(path, locale);
            Template t = conf.isCacheEnable() ? templateMap.get(cacheKey) : null;
            if (t != null) {
                return t;
            }
            for (String candidatePath : LocalizeUtil.getCandidatePaths(path, locale)) {
                if (t == null) {
                    logger.info("Initializing template " + candidatePath);
                    InputStream input = searchResource(candidatePath, "/");
                    if (input == null) {
                        continue;
                    }
                    t = new Template(candidatePath, input);
                    Template pre = templateMap.putIfAbsent(cacheKey, t);
                    if (pre != null) {
                        t = pre;
                    }
                }
                return t;
            }
            // TODO mayby we should return a null? So that caller can identify
            // the situation of template load error or template not found.
            throw new TemplateException(String.format("Template %s not found.", path));
        } catch (Exception e) {
            throw new TemplateException(path + " resolve error", e);
        }
    }

}
