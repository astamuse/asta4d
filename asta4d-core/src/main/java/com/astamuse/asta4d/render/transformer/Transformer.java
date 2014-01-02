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

import com.astamuse.asta4d.render.test.TestableRendering;

public abstract class Transformer<T> implements TestableRendering {

    // I want Optional in java 8
    private boolean originalDataConfigured = false;

    private Object originalData = null;

    private T content;

    public Transformer(T content) {
        this.content = content;
    }

    public Transformer(T content, Object originalData) {
        this.content = content;
        this.originalDataConfigured = true;
        this.originalData = originalData;
    }

    public Element invoke(Element elem) {
        return transform(elem, content);
    }

    protected abstract Element transform(Element elem, T content);

    @Override
    public String toString() {
        return this.getClass().getName() + ":[" + this.content.toString() + "]";
    }

    public T getContent() {
        return content;
    }

    @Override
    public Object retrieveTestableData() {
        if (originalDataConfigured) {
            return originalData;
        } else {
            return content;
        }
    }

    public void setOringialData(Object originalData) {
        this.originalData = originalData;
        this.originalDataConfigured = true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((content == null) ? 0 : content.hashCode());
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
        Transformer other = (Transformer) obj;
        if (content == null) {
            if (other.content != null)
                return false;
        } else if (!content.equals(other.content))
            return false;
        return true;
    }

}
