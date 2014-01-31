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
 * This node is intended to be used for a variety of purposes, such as combining multi nodes to a single node or being a place holder.
 * 
 * @author e-ryu
 * 
 */
public class GroupNode extends ExtNode {

    public GroupNode() {
        this(ExtNodeConstants.GROUP_NODE_ATTR_TYPE_FAKE);
    }

    public GroupNode(String type) {
        super(ExtNodeConstants.GROUP_NODE_TAG);
        this.attr(ExtNodeConstants.GROUP_NODE_ATTR_TYPE, type);
    }
}
