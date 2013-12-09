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

import com.astamuse.asta4d.render.test.TestableElementSetter;

/**
 * A ChildReplacer will empty the target Element first, then add the new child
 * node to the target element.
 * 
 * @author e-ryu
 * 
 */
public class ChildReplacer implements ElementSetter, TestableElementSetter {

    private Element newChild;

    /**
     * Constructor
     * 
     * @param newChild
     *            the new child node
     */
    public ChildReplacer(Element newChild) {
        this.newChild = newChild;
    }

    @Override
    public void set(Element elem) {
        elem.empty();
        elem.appendChild(newChild);
    }

    @Override
    public String toString() {
        String s = "replace the children to:{\n" + newChild.toString() + "\n}";
        return s;
    }

    @Override
    public Object retrieveTestableData() {
        return newChild;
    }

}
