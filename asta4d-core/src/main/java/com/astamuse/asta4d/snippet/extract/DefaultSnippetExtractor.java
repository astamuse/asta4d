/*
 * Copyright 2012 astamuse company,Ltd.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */

package com.astamuse.asta4d.snippet.extract;

import java.util.concurrent.ConcurrentHashMap;

import com.astamuse.asta4d.snippet.SnippetDeclarationInfo;

/**
 * Extract snippet declaration as the format of "xxx.yyy:zzz", if "zzz" is not specified, "render" will be used. "xxx.yyy" which is before
 * colon will be treated as snippet name (usually as class name or a id of a certain class) and the part after colon will be treated as
 * method name.
 * 
 * Additionally, "::" can be used as separator of method from version 1.1 to keep homogeneous grammar with Java 8's method reference.
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
        String[] sa = renderDeclaration.split("::|:");
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
