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
package com.astamuse.asta4d.util.i18n;

import java.util.Locale;

import com.astamuse.asta4d.util.i18n.pattern.JDKResourceBundleMessagePatternRetriever;
import com.astamuse.asta4d.util.i18n.pattern.MessagePatternRetriever;

/**
 * The sub class of this class should not return null in all the getMessage methods. Returns empty string or key instead.
 * 
 * 
 * @author e-ryu
 * 
 */
public abstract class I18nMessageHelper {

    private MessagePatternRetriever messagePatternRetriever;

    public I18nMessageHelper() {
        this(new JDKResourceBundleMessagePatternRetriever());
    }

    public I18nMessageHelper(MessagePatternRetriever messagePatternRetriever) {
        this.messagePatternRetriever = messagePatternRetriever;
    }

    public MessagePatternRetriever getMessagePatternRetriever() {
        return messagePatternRetriever;
    }

    public void setMessagePatternRetriever(MessagePatternRetriever messagePatternRetriever) {
        this.messagePatternRetriever = messagePatternRetriever;
    }

    /**
     * retrieve message by given key
     * 
     * @param key
     * @return retrieved message, empty string or the given key if message not found(cannot be null)
     */
    public abstract String getMessage(String key);

    /**
     * retrieve message by given locale and key
     * 
     * @param locale
     * @param key
     * @return retrieved message, empty string or the given key if message not found(cannot be null)
     */
    public abstract String getMessage(Locale locale, String key);

    /**
     * retrieve message by given key
     * 
     * @param key
     * @param defaultPattern
     * @return retrieved message, return defaultPattern#toString() if message not found
     */
    public abstract String getMessageWithDefault(String key, Object defaultPattern);

    /**
     * retrieve message by given locale and key
     * 
     * @param locale
     * @param key
     * @param defaultPattern
     * @return retrieved message, return defaultPattern#toString() if message not found
     */
    public abstract String getMessageWithDefault(Locale locale, String key, Object defaultPattern);
}
