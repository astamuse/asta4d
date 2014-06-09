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

package com.astamuse.asta4d.template;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.astamuse.asta4d.Configuration;
import com.astamuse.asta4d.Context;
import com.astamuse.asta4d.template.TemplateResolver.TemplateInfo;
import com.astamuse.asta4d.util.MultiSearchPathResourceLoader;
import com.astamuse.asta4d.util.i18n.LocalizeUtil;

//TODO internationalization
public abstract class TemplateResolver extends MultiSearchPathResourceLoader<TemplateInfo> {

    private final static ConcurrentHashMap<String, Template> defaultCachedTemplateMap = new ConcurrentHashMap<String, Template>();

    private static Template NotFoundHolder;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private Map<String, Template> getCachedTemplateMap() {
        Context context = Context.getCurrentThreadContext();
        Configuration conf = Configuration.getConfiguration();
        if (conf.isCacheEnable()) {
            return defaultCachedTemplateMap;
        } else {
            // for debug, if we do not cache it in context, some templates will
            // be probably initialized for hundreds times
            // thus there will be hundreds logs of template initializing in info
            // level.
            String key = TemplateResolver.class.getName() + "##template-cache-map";
            Map<String, Template> contextCachedMap = context.getData(key);
            if (contextCachedMap == null) {
                contextCachedMap = new ConcurrentHashMap<>();
                context.setData(key, contextCachedMap);
            }
            return contextCachedMap;
        }
    }

    private static Template getNotFoundHolder() throws TemplateException {
        if (NotFoundHolder == null) {
            String dummyHolderContent = "##NOT-FOUND-HOLDER##";
            NotFoundHolder = new Template(dummyHolderContent, new ByteArrayInputStream(dummyHolderContent.getBytes()));
        }
        return NotFoundHolder;
    }

    public Template findTemplate(String path) throws TemplateException {
        try {

            Map<String, Template> cachedTemplateMap = getCachedTemplateMap();

            Locale locale = Context.getCurrentThreadContext().getCurrentLocale();
            String cacheKey = LocalizeUtil.createLocalizedKey(path, locale);
            Template t = cachedTemplateMap.get(cacheKey);
            if (t != null) {
                if (t == NotFoundHolder) {
                    return null;
                } else {
                    return t;
                }
            }
            logger.info("Initializing template " + path);
            TemplateInfo info = searchResource("/", LocalizeUtil.getCandidatePaths(path, locale));
            if (info == null) {
                cachedTemplateMap.put(cacheKey, getNotFoundHolder());
                return null;
            }
            InputStream input = info.getInput();
            if (input == null) {
                cachedTemplateMap.put(cacheKey, getNotFoundHolder());
                return null;
            }
            try {
                t = new Template(info.getPath(), input);
            } finally {
                // we have to close the input stream to avoid file lock
                try {
                    input.close();
                } catch (Exception ex) {
                    logger.error("Error occured when close input stream of " + info.getPath(), ex);
                }
            }
            cachedTemplateMap.put(cacheKey, t);
            return t;
        } catch (Exception e) {
            throw new TemplateException(path + " resolve error", e);
        }
    }

    protected TemplateInfo createTemplateInfo(String path, InputStream input) {
        if (input == null) {
            return null;
        }
        return new TemplateInfo(path, input);
    }

    public static class TemplateInfo {

        private final String path;

        private final InputStream input;

        private TemplateInfo(String path, InputStream input) {
            this.path = path;
            this.input = input;
        }

        private String getPath() {
            return path;
        }

        private InputStream getInput() {
            return input;
        }

    }

}
