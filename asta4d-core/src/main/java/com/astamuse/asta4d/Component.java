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

package com.astamuse.asta4d;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;

import com.astamuse.asta4d.extnode.GroupNode;
import com.astamuse.asta4d.render.AttributeSetter;
import com.astamuse.asta4d.render.RenderUtil;
import com.astamuse.asta4d.template.Template;
import com.astamuse.asta4d.template.TemplateResolver;

public class Component {

    public static abstract class AttributesRequire {

        private List<AttributeSetter> attrList = new ArrayList<>();

        public AttributesRequire() {
            this.prepareAttributes();
        }

        protected void add(String attr, Object value) {
            attrList.add(new AttributeSetter(attr, value));
        }

        List<AttributeSetter> getAttrList() {
            return attrList;
        }

        protected abstract void prepareAttributes();

    }

    private Element renderedElement;

    public Component(Element elem, AttributesRequire attrs) throws Exception {
        Document doc = new Document("");
        doc.appendElement("body");
        doc.body().appendChild(elem);
        renderedElement = renderTemplate(doc, attrs);
    }

    public Component(Element elem) throws Exception {
        this(elem, null);
    }

    public Component(String path, AttributesRequire attrs) throws Exception {
        Configuration conf = Configuration.getConfiguration();
        TemplateResolver templateResolver = conf.getTemplateResolver();
        Template template = templateResolver.findTemplate(path);
        renderedElement = renderTemplate(template.getDocumentClone(), attrs);
    }

    public Component(String path) throws Exception {
        this(path, null);
    }

    protected Element renderTemplate(Document doc, AttributesRequire attrs) throws Exception {
        if (attrs != null) {
            List<AttributeSetter> attrList = attrs.getAttrList();
            Element body = doc.body();
            for (AttributeSetter attributeSetter : attrList) {
                attributeSetter.set(body);
            }
        }

        RenderUtil.applySnippets(doc);

        Element grp = new GroupNode();
        List<Node> children = new ArrayList<>(doc.body().childNodes());
        for (Node node : children) {
            node.remove();
            grp.appendChild(node);
        }

        return grp;
    }

    public Element toElement() {
        return renderedElement.clone();
    }

    public String toHtml() {
        Document doc = new Document("");
        doc.appendChild(toElement());
        RenderUtil.applyMessages(doc);
        RenderUtil.applyClearAction(doc, true);
        return doc.html();
    }

    public String toString() {
        return toHtml();
    }

}
