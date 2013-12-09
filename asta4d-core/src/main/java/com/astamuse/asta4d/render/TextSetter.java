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

import com.astamuse.asta4d.render.test.TestableElementSetter;
import com.astamuse.asta4d.util.Asta4DWarningException;

/**
 * A TextSetter will empty the target element at first, then add a new text node
 * to the target element
 * 
 * @author e-ryu
 * 
 */
public class TextSetter implements ElementSetter, TestableElementSetter {

    private final static Logger logger = LoggerFactory.getLogger(TextSetter.class);

    private Object originContent;

    private String renderText;

    /**
     * Constructor
     * 
     * @param content
     *            the content wanted to be rendered
     */
    public TextSetter(Object content) {
        this.originContent = content;
        this.renderText = content2Str(content);
    }

    private final static String content2Str(Object content) {
        if (content == null) {
            String msg = "Trying to render a null String";
            // we want to get a information of where the null is passed, so we
            // create a exception to get the calling stacks
            Exception ex = new Asta4DWarningException(msg);
            logger.warn(msg, ex);
        }
        return content == null ? "" : content.toString();
    }

    @Override
    public void set(Element elem) {
        elem.empty();
        elem.appendText(renderText);
    }

    @Override
    public String toString() {
        return renderText;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((originContent == null) ? 0 : originContent.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        TextSetter other = (TextSetter) obj;
        if (originContent == null) {
            if (other.originContent != null)
                return false;
        } else if (!originContent.equals(other.originContent))
            return false;
        return true;
    }

    @Override
    public Object retrieveTestableData() {
        return originContent;
    }

}
