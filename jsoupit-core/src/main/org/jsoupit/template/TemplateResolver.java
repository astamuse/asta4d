package org.jsoupit.template;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ConcurrentHashMap;

//TODO internationalization and exception of template not found
public abstract class TemplateResolver {

    protected final static ConcurrentHashMap<String, Template> templateMap = new ConcurrentHashMap<String, Template>();

    public Template findTemplate(String path) throws IOException {
        try {
            Configuration conf = Context.getCurrentThreadContext().getConfiguration();
            if (conf.isCacheEnable()) {
                Template t = templateMap.get(path);
                if (t == null) {
                    t = new Template(path, loadTemplate(path));
                    Template pre = templateMap.putIfAbsent(path, t);
                    if (pre != null) {
                        t = pre;
                    }
                }
                return t;
            } else {
                return new Template(path, loadTemplate(path));
            }
        } catch (Exception e) {
            throw new IOException(path + " resolve error", e);
        }
    }

    // TODO where to close the InputStream?
    protected abstract InputStream loadTemplate(String path);
}
