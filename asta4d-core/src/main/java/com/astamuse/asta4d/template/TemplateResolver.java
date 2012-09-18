package com.astamuse.asta4d.template;

import java.io.InputStream;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.astamuse.asta4d.Configuration;
import com.astamuse.asta4d.Context;

//TODO internationalization
public abstract class TemplateResolver extends MultiSearchPathResourceLoader<InputStream> {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    protected final static ConcurrentHashMap<String, Template> templateMap = new ConcurrentHashMap<String, Template>();

    public Template findTemplate(String path) throws TemplateException {
        try {
            Configuration conf = Context.getCurrentThreadContext().getConfiguration();
            if (conf.isCacheEnable()) {
                Template t = templateMap.get(path);
                if (t == null) {
                    logger.info("Initializing template " + path);
                    t = new Template(path, searchResource(path, "/"));
                    Template pre = templateMap.putIfAbsent(path, t);
                    if (pre != null) {
                        t = pre;
                    } else {
                        String msg = String.format("Template %s not found.", path);
                        throw new TemplateException(msg);
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
