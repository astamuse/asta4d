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
package com.astamuse.asta4d.render;

import org.jsoup.nodes.Element;

import com.astamuse.asta4d.render.transformer.ElementSetterTransformer;

public abstract class ElementNotFoundHandler extends Renderer {

    private final static ElementSetterTransformer DoNothingTransformer = new ElementSetterTransformer(new ElementSetter() {
        @Override
        public void set(Element elem) {
            // do nothing
        }
    });

    public ElementNotFoundHandler(String selector) {
        super(selector, DoNothingTransformer);
    }

    @Override
    RendererType getRendererType() {
        return RendererType.ELEMENT_NOT_FOUND_HANDLER;
    }

    @Override
    public String toString() {
        return "ElementNotFoundHandler";
    }

    public abstract Renderer alternativeRenderer();
}
