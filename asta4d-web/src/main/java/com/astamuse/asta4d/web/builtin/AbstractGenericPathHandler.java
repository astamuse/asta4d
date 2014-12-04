/*
 * Copyright 2014 astamuse company,Ltd.
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
package com.astamuse.asta4d.web.builtin;

import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.io.FilenameUtils;

import com.astamuse.asta4d.Context;
import com.astamuse.asta4d.web.WebApplicationContext;
import com.astamuse.asta4d.web.dispatch.mapping.UrlMappingRule;

public abstract class AbstractGenericPathHandler {
    public final static String VAR_BASEPATH = "basePath";

    private final static ConcurrentHashMap<String, String> genericMapResult = new ConcurrentHashMap<>();

    private final static String NullHolder = AbstractGenericPathHandler.class.getName() + "#NULL";

    private String _basePath = null;

    public AbstractGenericPathHandler() {
    }

    public AbstractGenericPathHandler(String basePath) {
        this._basePath = basePath;
    }

    public String convertPath(UrlMappingRule currentRule) {
        WebApplicationContext context = Context.getCurrentThreadContext();
        String uri = context.getAccessURI();

        String targetPath = genericMapResult.get(uri);
        if (targetPath != null) {
            if (targetPath.equals(NullHolder)) {
                return null;
            } else {
                return targetPath;
            }
        } else {
            String basePath = context.getData(WebApplicationContext.SCOPE_PATHVAR, VAR_BASEPATH);
            if (basePath == null) {
                basePath = _basePath;
            }

            if (basePath == null) {// default from web context root
                targetPath = uri;
            } else {

                basePath = FilenameUtils.normalize(basePath, true);

                String src = currentRule.getSourcePath();
                // convert for /**/*
                String mask = "/**/*";
                int idx = src.indexOf(mask);
                if (idx >= 0) {
                    String parentPath = src.substring(0, idx);
                    String childPath = uri.substring(parentPath.length());

                    if (basePath.endsWith("/")) {
                        basePath = basePath.substring(0, basePath.length() - 1);
                    }
                    if (!childPath.startsWith("/")) {
                        childPath = "/" + childPath;
                    }
                    targetPath = basePath + childPath;
                } else {// be a one 2 one mapping
                    targetPath = basePath;
                }
            }

            if (fileNameSecurityCheck(targetPath)) {
                genericMapResult.put(uri, targetPath);
                return targetPath;
            } else {
                genericMapResult.put(uri, NullHolder);
                return null;
            }
        }
    }

    private boolean fileNameSecurityCheck(String path) {
        // we do not allow any unnormalized path for security reason
        String normalizedPath = FilenameUtils.normalize(path, true);
        return path.equals(normalizedPath);
    }
}
