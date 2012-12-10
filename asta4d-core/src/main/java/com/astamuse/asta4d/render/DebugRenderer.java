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

package com.astamuse.asta4d.render;

import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.astamuse.asta4d.Context;
import com.astamuse.asta4d.render.transformer.ElementSetterTransformer;

/**
 * When this renderer is applied, the current rendering element (see
 * {@link Context#setCurrentRenderingElement(Element)}) will be output by logger
 * in debug level.
 * 
 * @author e-ryu
 * 
 */
public class DebugRenderer extends Renderer {

    private final static String CurrentNodeSelector = "DebugRenderer-CurrentNodeSelector";

    final static Logger logger = LoggerFactory.getLogger(DebugRenderer.class);

    public DebugRenderer(final String logMessage) {
        super(CurrentNodeSelector + ":" + logMessage, new ElementSetterTransformer(new ElementSetter() {
            @Override
            public void set(Element elem) {
                String logStr = logMessage + ":\n" + elem.toString();
                logger.debug(logStr);
            }
        }));
    }
}
