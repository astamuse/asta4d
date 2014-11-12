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
import com.astamuse.asta4d.render.transformer.Transformer;

/**
 * When this renderer is applied, the current rendering element (see {@link Context#setCurrentRenderingElement(Element)}) will be output by
 * logger in debug level.
 * 
 * @author e-ryu
 * 
 */
public class DebugRenderer extends Renderer {

    private static final class DebugTransformer extends Transformer<Object> {

        Logger logger = null;
        String logMessage = null;
        String creationSiteInfo = null;

        public DebugTransformer() {
            super(null);
        }

        @Override
        protected Element transform(Element elem, Object content) {
            String logStr = logMessage + "( " + creationSiteInfo + " ):\n" + elem.toString();
            logger.debug(logStr);

            // we have to clone a new element, which is how transformer works.
            Element newElem = elem.clone();
            return newElem;
        }

    }

    final static Logger DefaultLogger = LoggerFactory.getLogger(DebugRenderer.class);

    public DebugRenderer(Logger logger, String logMessage) {
        super(":root", new DebugTransformer());
        DebugTransformer dts = (DebugTransformer) getTransformerList().get(0);
        dts.logMessage = logMessage;
        dts.creationSiteInfo = getCreationSiteInfo();
        dts.logger = logger;
    }

    /*
    @Override
    RendererType getRendererType() {
        return RendererType.DEBUG;
    }
    */

}
