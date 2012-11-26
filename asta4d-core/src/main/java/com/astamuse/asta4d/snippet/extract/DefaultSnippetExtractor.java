package com.astamuse.asta4d.snippet.extract;

import java.util.concurrent.ConcurrentHashMap;

import com.astamuse.asta4d.snippet.SnippetDeclarationInfo;

/**
 * Extract snippet declaration as the format of "xxx.yyy:zzz", if "zzz" is not
 * specified, "render" will be used. "xxx.yyy" which is before colon will be
 * treated as snippet name (usually as class name or a id of a certain class)
 * and the part after colon will be treated as method name.
 * 
 * @author e-ryu
 * 
 */
public class DefaultSnippetExtractor implements SnippetExtractor {

    private final static ConcurrentHashMap<String, SnippetDeclarationInfo> infoCache = new ConcurrentHashMap<>();

    @Override
    public SnippetDeclarationInfo extract(String renderDeclaration) {
        SnippetDeclarationInfo info = infoCache.get(renderDeclaration);
        if (info == null) {
            info = _extract(renderDeclaration);
            infoCache.put(renderDeclaration, info);
        }
        return info;
    }

    private SnippetDeclarationInfo _extract(String renderDeclaration) {
        String snippetClass, snippetMethod;
        String[] sa = renderDeclaration.split(":");
        if (sa.length < 2) {
            snippetClass = sa[0];
            snippetMethod = "render";
        } else {
            snippetClass = sa[0];
            snippetMethod = sa[1];
        }
        return new SnippetDeclarationInfo(snippetClass, snippetMethod);
    }

}
