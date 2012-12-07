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

package com.astamuse.asta4d.extnode;

/**
 * This Node is intended to be used for dynamically creating snippet. It can be
 * created by a snippet Class information or a plain text renderer declaration.
 * 
 * @author e-ryu
 * 
 */
public class SnippetNode extends ExtNode {

    /**
     * Constructor
     * 
     * @param renderClass
     *            a snippet class
     */
    public SnippetNode(Class<?> renderClass) {
        this(renderClass.getName());
    }

    /**
     * 
     * @param renderer
     *            a plain text renderer declaration
     */
    public SnippetNode(String renderer) {
        super(ExtNodeConstants.SNIPPET_NODE_TAG);
        this.attr(ExtNodeConstants.SNIPPET_NODE_ATTR_STATUS, ExtNodeConstants.SNIPPET_NODE_ATTR_STATUS_READY);
        this.attr(ExtNodeConstants.SNIPPET_NODE_ATTR_RENDER, renderer);
    }

}
