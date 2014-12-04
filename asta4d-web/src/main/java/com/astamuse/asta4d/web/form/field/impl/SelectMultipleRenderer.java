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
package com.astamuse.asta4d.web.form.field.impl;

import org.jsoup.nodes.Element;
import org.jsoup.parser.Tag;

public class SelectMultipleRenderer extends AbstractSelectRenderer {
    protected Element createAlternativeDisplayElement(String nonNullString) {
        Element span = new Element(Tag.valueOf("div"), "");
        span.text(nonNullString);
        return span;
    }
}
