package org.jsoupit.template.snippet.extract;

import java.util.concurrent.ConcurrentHashMap;

import org.jsoupit.template.snippet.SnippetInfo;

/**
 * Extract snippet declaration as the format of "xxx.yyy:zzz", if "zzz" is not
 * specified, "render" will be used. "xxx.yyy" which is before colon will be
 * treated as class name and the part after colon will be treated as method
 * name.
 * 
 * @author e-ryu
 * 
 */
public class DefaultSnippetExtractor implements SnippetExtractor {

    private final static ConcurrentHashMap<String, SnippetInfo> infoCache = new ConcurrentHashMap<>();

    @Override
    public SnippetInfo extract(String renderDeclaration) {
        SnippetInfo info = infoCache.get(renderDeclaration);
        if (info == null) {
            info = _extract(renderDeclaration);
            infoCache.put(renderDeclaration, info);
        }
        return info;
    }

    private SnippetInfo _extract(String renderDeclaration) {
        String snippetClass, snippetMethod;
        String[] sa = renderDeclaration.split(":");
        if (sa.length < 2) {
            snippetClass = sa[0];
            snippetMethod = "render";
        } else {
            snippetClass = sa[0];
            snippetMethod = sa[1];
        }
        return new SnippetInfo(snippetClass, snippetMethod);
    }

}
