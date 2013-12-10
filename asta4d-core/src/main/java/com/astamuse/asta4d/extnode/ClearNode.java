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

import com.astamuse.asta4d.render.SpecialRenderer;

/**
 * 
 * A ClearNode will be removed after rendering. There is no warranty about when
 * a ClearNode will be removed but it was warranted that a ClearNode will be
 * eventually removed at the last of rendering process. <br>
 * Further, a formal html element with attribute "afd:clear" will be treated as
 * a ClearNode too.
 * 
 * This class has been deprecated and you don't need to use this class for
 * removing nodes, {@link SpecialRenderer#Clear} can be used for remove a
 * certain node, and a null value passed to Renderer will remove the target node
 * too.
 * 
 * @author e-ryu
 * @see SpecialRenderer#Clear
 */
@Deprecated
public class ClearNode extends GroupNode {

    public ClearNode() {
        super();
        this.attr(ExtNodeConstants.ATTR_CLEAR, "");
    }

}
