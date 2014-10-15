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

package com.astamuse.asta4d.util.i18n;

import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.astamuse.asta4d.Configuration;
import com.astamuse.asta4d.Context;
import com.astamuse.asta4d.util.i18n.format.PlaceholderFormatter;

public abstract class ResourceBundleHelperBase {
    private final static Logger LOGGER = LoggerFactory.getLogger(ResourceBundleHelperBase.class);

    private Locale locale = Context.getCurrentThreadContext().getCurrentLocale();

    private PlaceholderFormatter formatter = Configuration.getConfiguration().getPlaceholderFormatter();

    public ResourceBundleHelperBase(Locale locale, PlaceholderFormatter formatter) {
        this.locale = locale;
        this.formatter = formatter;
    }

    public ResourceBundleHelperBase(Locale locale) {
        this(locale, Configuration.getConfiguration().getPlaceholderFormatter());
    }

    public ResourceBundleHelperBase(PlaceholderFormatter formatter) {
        this(Context.getCurrentThreadContext().getCurrentLocale(), formatter);
    }

    public ResourceBundleHelperBase() {
        this(Context.getCurrentThreadContext().getCurrentLocale(), Configuration.getConfiguration().getPlaceholderFormatter());
    }

    protected Locale getLocale() {
        return locale;
    }

    protected PlaceholderFormatter getFormatter() {
        return this.formatter;
    }

}
