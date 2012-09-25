package com.astamuse.asta4d.template;

import java.io.InputStream;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.astamuse.asta4d.Configuration;
import com.astamuse.asta4d.Context;
import com.astamuse.asta4d.util.MultiSearchPathResourceLoader;

//TODO internationalization
public abstract class TemplateResolver extends MultiSearchPathResourceLoader<InputStream> {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    protected final static ConcurrentHashMap<String, Template> templateMap = new ConcurrentHashMap<String, Template>();

    public Template findTemplate(String path) throws TemplateException {
        try {
            Configuration conf = Context.getCurrentThreadContext().getConfiguration();
            Template t = conf.isCacheEnable() ? templateMap.get(path) : null;
            if (t == null) {
                logger.info("Initializing template " + path);
                InputStream input = searchResource(path, "/");
                if (input == null) {
                    String msg = String.format("Template %s not found.", path);
                    throw new TemplateException(msg);
                }
                t = new Template(path, input);
                Template pre = templateMap.putIfAbsent(path, t);
                if (pre != null) {
                    t = pre;
                }
            }
            return t;
        } catch (Exception e) {
            throw new TemplateException(path + " resolve error", e);
        }
    }

}
