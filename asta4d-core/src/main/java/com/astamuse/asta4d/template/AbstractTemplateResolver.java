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

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.parser.Asta4DTagSupportHtmlTreeBuilder;
import org.jsoup.parser.Parser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.astamuse.asta4d.Configuration;
import com.astamuse.asta4d.Context;
import com.astamuse.asta4d.template.AbstractTemplateResolver.TemplateInfo;
import com.astamuse.asta4d.util.MemorySafeResourceCache;
import com.astamuse.asta4d.util.MemorySafeResourceCache.ResouceHolder;
import com.astamuse.asta4d.util.MultiSearchPathResourceLoader;
import com.astamuse.asta4d.util.i18n.LocalizeUtil;

public abstract class AbstractTemplateResolver extends MultiSearchPathResourceLoader<TemplateInfo>implements TemplateResolver {

    private final MemorySafeResourceCache<String, Template> defaultTemplateCache = new MemorySafeResourceCache<String, Template>();

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private MemorySafeResourceCache<String, Template> retrieveTemplateCache() {
        Context context = Context.getCurrentThreadContext();
        Configuration conf = Configuration.getConfiguration();
        if (conf.isCacheEnable()) {
            return defaultTemplateCache;
        } else {
            // for debug, if we do not cache it in context, some templates will
            // be probably initialized for hundreds times
            // thus there will be hundreds logs of template initializing in info
            // level.
            String key = this.getClass().getName() + "##template-cache-map";
            MemorySafeResourceCache<String, Template> contextCachedMap = context.getData(key);
            if (contextCachedMap == null) {
                contextCachedMap = new MemorySafeResourceCache<>();
                context.setData(key, contextCachedMap);
            }
            return contextCachedMap;
        }
    }

    /**
     * 
     * @param path
     * @return
     * @throws TemplateException
     *             error occurs when parsing template file
     * @throws TemplateNotFoundException
     *             template file does not exist
     */
    public Template findTemplate(String path) throws TemplateException, TemplateNotFoundException {
        try {

            MemorySafeResourceCache<String, Template> templateCache = retrieveTemplateCache();

            Locale locale = LocalizeUtil.defaultWhenNull(null);
            String cacheKey = LocalizeUtil.createLocalizedKey(path, locale);

            ResouceHolder<Template> resource = templateCache.get(cacheKey);

            if (resource != null) {
                if (resource.exists()) {
                    return resource.get();
                } else {
                    throw new TemplateNotFoundException(path);
                }
            }
            logger.info("Initializing template " + path);
            TemplateInfo info = searchResource("/", LocalizeUtil.getCandidatePaths(path, locale));
            if (info == null) {
                templateCache.put(cacheKey, null);
                throw new TemplateNotFoundException(path);
            }
            InputStream input = info.getInput();
            if (input == null) {
                templateCache.put(cacheKey, null);
                throw new TemplateNotFoundException(path);
            }

            try {
                Template t = createTemplate(info);
                templateCache.put(cacheKey, t);
                return t;
            } finally {
                // we have to close the input stream to avoid file lock
                try {
                    input.close();
                } catch (Exception ex) {
                    logger.error("Error occured when close input stream of " + info.getActualPath(), ex);
                }
            }

        } catch (TemplateNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new TemplateException(path + " resolve error", e);
        }
    }

    protected Template createTemplate(TemplateInfo templateInfo) throws TemplateException, TemplateNotFoundException {
        try {
            Document doc = parse(templateInfo.getInput());
            return new Template(templateInfo.getActualPath(), doc);
        } catch (IOException e) {
            String msg = String.format("Template %s parsing failed.", templateInfo.getActualPath());
            throw new TemplateException(msg, e);
        }
    }

    protected Document parse(InputStream input) throws IOException {
        return Jsoup.parse(input, "UTF-8", "", new Parser(new Asta4DTagSupportHtmlTreeBuilder()));
    }

    protected TemplateInfo createTemplateInfo(String path, InputStream input) {
        if (input == null) {
            return null;
        }
        return new TemplateInfo(path, input);
    }

    public static class TemplateInfo {

        private final String actualPath;

        private final InputStream input;

        public TemplateInfo(String path, InputStream input) {
            this.actualPath = path;
            this.input = input;
        }

        public String getActualPath() {
            return actualPath;
        }

        public InputStream getInput() {
            return input;
        }

    }

}
