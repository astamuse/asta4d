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

package com.astamuse.asta4d.render.concurrent;

import com.astamuse.asta4d.render.Renderer;

public class FutureRendererHolder {

    String renderDeclaration;
    String snippetRefId;
    Renderer renderer;

    public FutureRendererHolder(String renderDeclaration, String snippetRefId, Renderer renderer) {
        super();
        this.snippetRefId = snippetRefId;
        this.renderer = renderer;
    }

    public String getRenderDeclaration() {
        return renderDeclaration;
    }

    public void setRenderDeclaration(String renderDeclaration) {
        this.renderDeclaration = renderDeclaration;
    }

    public String getSnippetRefId() {
        return snippetRefId;
    }

    public void setSnippetRefId(String snippetRefId) {
        this.snippetRefId = snippetRefId;
    }

    public Renderer getRenderer() {
        return renderer;
    }

    public void setRenderer(Renderer renderer) {
        this.renderer = renderer;
    }

}
