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

import org.jsoup.nodes.Element;

import com.astamuse.asta4d.extnode.GroupNode;
import com.astamuse.asta4d.render.RenderUtil;
import com.astamuse.asta4d.render.Renderer;

public class RendererTransformer extends Transformer<Renderer> {

    public RendererTransformer(Renderer content) {
        super(content);
    }

    @Override
    protected Element transform(Element elem, Renderer content) {
        Element result = elem.clone();

        // add a dummy parent so that the result element can be replaced by the
        // sub renderer.
        GroupNode wrapper = new GroupNode();
        wrapper.appendChild(result);
        RenderUtil.apply(result, content);

        return wrapper;
    }

}
