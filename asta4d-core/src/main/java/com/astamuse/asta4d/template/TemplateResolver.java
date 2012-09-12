package com.astamuse.asta4d.template;

import java.io.InputStream;
import java.util.concurrent.ConcurrentHashMap;

import com.astamuse.asta4d.Configuration;
import com.astamuse.asta4d.Context;

//TODO internationalization and exception of template not found
public abstract class TemplateResolver extends MultiSearchPathResourceLoader<InputStream> {

    protected final static ConcurrentHashMap<String, Template> templateMap = new ConcurrentHashMap<String, Template>();

    public Template findTemplate(String path) throws TemplateException {
        try {
            Configuration conf = Context.getCurrentThreadContext().getConfiguration();
            if (conf.isCacheEnable()) {
                Template t = templateMap.get(path);
                if (t == null) {
                    t = new Template(path, searchResource(path, "/"));
                    Template pre = templateMap.putIfAbsent(path, t);
                    if (pre != null) {
                        t = pre;
                    }
                }
                return t;
            } else {
                return new Template(path, searchResource(path, "/"));
            }
        } catch (Exception e) {
            throw new TemplateException(path + " resolve error", e);
        }
    }

}
