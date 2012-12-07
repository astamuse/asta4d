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

package com.astamuse.asta4d.render.transformer;

import java.util.concurrent.Future;

import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.astamuse.asta4d.render.ElementSetter;
import com.astamuse.asta4d.render.Renderer;
import com.astamuse.asta4d.render.TextSetter;
import com.astamuse.asta4d.util.Asta4DWarningException;

public class TransformerFactory {

    private final static Logger logger = LoggerFactory.getLogger(TransformerFactory.class);

    public final static Transformer<?> generateTransformer(Object action) {
        Transformer<?> transformer;
        if (action instanceof String) {
            transformer = new ElementSetterTransformer(new TextSetter(action.toString()));
        } else if (action instanceof ElementSetter) {
            transformer = new ElementSetterTransformer((ElementSetter) action);
        } else if (action instanceof Renderer) {
            transformer = new RendererTransformer((Renderer) action);
        } else if (action instanceof Future) {
            transformer = new FutureTransformer((Future<?>) action);
        } else if (action instanceof Element) {
            transformer = new ElementTransformer((Element) action);
        } else {
            String msg = "Unsupported type found in transformer generation:" + action.getClass().getName();
            Asta4DWarningException awe = new Asta4DWarningException(msg);
            logger.warn(msg, awe);
            transformer = new ElementSetterTransformer(new TextSetter(action.toString()));
        }
        return transformer;
    }

}
