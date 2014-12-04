/*
 * Copyright 2014 astamuse company,Ltd.
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
package com.astamuse.asta4d.render.test;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.DataNode;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.parser.Tag;
import org.jsoup.select.Elements;
import org.jsoup.select.NodeVisitor;

import com.astamuse.asta4d.util.ElementUtil;

public class TestableElementWrapper extends Element {

    private Element originElement;

    public TestableElementWrapper(Element originElement) {
        super(Tag.valueOf("div"), "");
        this.originElement = originElement;
    }

    public final static TestableElementWrapper parse(String html) {
        return new TestableElementWrapper(ElementUtil.parseAsSingle(html));
    }

    public String attr(String attributeKey) {
        return originElement.attr(attributeKey);
    }

    public String nodeName() {
        return originElement.nodeName();
    }

    public String tagName() {
        return originElement.tagName();
    }

    public Element tagName(String tagName) {
        return originElement.tagName(tagName);
    }

    public Tag tag() {
        return originElement.tag();
    }

    public Attributes attributes() {
        return originElement.attributes();
    }

    public boolean isBlock() {
        return originElement.isBlock();
    }

    public String id() {
        return originElement.id();
    }

    public Element attr(String attributeKey, String attributeValue) {
        return originElement.attr(attributeKey, attributeValue);
    }

    public boolean hasAttr(String attributeKey) {
        return originElement.hasAttr(attributeKey);
    }

    public Map<String, String> dataset() {
        return originElement.dataset();
    }

    public Node removeAttr(String attributeKey) {
        return originElement.removeAttr(attributeKey);
    }

    public String baseUri() {
        return originElement.baseUri();
    }

    public void setBaseUri(String baseUri) {
        originElement.setBaseUri(baseUri);
    }

    public Elements parents() {
        return originElement.parents();
    }

    public String absUrl(String attributeKey) {
        return originElement.absUrl(attributeKey);
    }

    public Element child(int index) {
        return originElement.child(index);
    }

    public Elements children() {
        return originElement.children();
    }

    public List<TextNode> textNodes() {
        return originElement.textNodes();
    }

    public Node childNode(int index) {
        return originElement.childNode(index);
    }

    public List<Node> childNodes() {
        return originElement.childNodes();
    }

    public List<DataNode> dataNodes() {
        return originElement.dataNodes();
    }

    public Document ownerDocument() {
        return originElement.ownerDocument();
    }

    public void remove() {
        originElement.remove();
    }

    public Elements select(String cssQuery) {
        return originElement.select(cssQuery);
    }

    public Element appendChild(Node child) {
        return originElement.appendChild(child);
    }

    public Element prependChild(Node child) {
        return originElement.prependChild(child);
    }

    public Element appendElement(String tagName) {
        return originElement.appendElement(tagName);
    }

    public Element prependElement(String tagName) {
        return originElement.prependElement(tagName);
    }

    public Element appendText(String text) {
        return originElement.appendText(text);
    }

    public Element prependText(String text) {
        return originElement.prependText(text);
    }

    public Node unwrap() {
        return originElement.unwrap();
    }

    public Element append(String html) {
        return originElement.append(html);
    }

    public Element prepend(String html) {
        return originElement.prepend(html);
    }

    public Element before(String html) {
        return originElement.before(html);
    }

    public void replaceWith(Node in) {
        originElement.replaceWith(in);
    }

    public Element before(Node node) {
        return originElement.before(node);
    }

    public Element after(String html) {
        return originElement.after(html);
    }

    public Element after(Node node) {
        return originElement.after(node);
    }

    public Element empty() {
        return originElement.empty();
    }

    public Element wrap(String html) {
        return originElement.wrap(html);
    }

    public Elements siblingElements() {
        return originElement.siblingElements();
    }

    public List<Node> siblingNodes() {
        return originElement.siblingNodes();
    }

    public Element nextElementSibling() {
        return originElement.nextElementSibling();
    }

    public Node nextSibling() {
        return originElement.nextSibling();
    }

    public Element previousElementSibling() {
        return originElement.previousElementSibling();
    }

    public Node previousSibling() {
        return originElement.previousSibling();
    }

    public int siblingIndex() {
        return originElement.siblingIndex();
    }

    public Element firstElementSibling() {
        return originElement.firstElementSibling();
    }

    public Integer elementSiblingIndex() {
        return originElement.elementSiblingIndex();
    }

    public Node traverse(NodeVisitor nodeVisitor) {
        return originElement.traverse(nodeVisitor);
    }

    public Element lastElementSibling() {
        return originElement.lastElementSibling();
    }

    public String outerHtml() {
        return originElement.outerHtml();
    }

    public Elements getElementsByTag(String tagName) {
        return originElement.getElementsByTag(tagName);
    }

    public Element getElementById(String id) {
        return originElement.getElementById(id);
    }

    public Elements getElementsByClass(String className) {
        return originElement.getElementsByClass(className);
    }

    public Elements getElementsByAttribute(String key) {
        return originElement.getElementsByAttribute(key);
    }

    public Elements getElementsByAttributeStarting(String keyPrefix) {
        return originElement.getElementsByAttributeStarting(keyPrefix);
    }

    public Elements getElementsByAttributeValue(String key, String value) {
        return originElement.getElementsByAttributeValue(key, value);
    }

    public Elements getElementsByAttributeValueNot(String key, String value) {
        return originElement.getElementsByAttributeValueNot(key, value);
    }

    public Elements getElementsByAttributeValueStarting(String key, String valuePrefix) {
        return originElement.getElementsByAttributeValueStarting(key, valuePrefix);
    }

    public Elements getElementsByAttributeValueEnding(String key, String valueSuffix) {
        return originElement.getElementsByAttributeValueEnding(key, valueSuffix);
    }

    public Elements getElementsByAttributeValueContaining(String key, String match) {
        return originElement.getElementsByAttributeValueContaining(key, match);
    }

    public Elements getElementsByAttributeValueMatching(String key, Pattern pattern) {
        return originElement.getElementsByAttributeValueMatching(key, pattern);
    }

    public Elements getElementsByAttributeValueMatching(String key, String regex) {
        return originElement.getElementsByAttributeValueMatching(key, regex);
    }

    public Elements getElementsByIndexLessThan(int index) {
        return originElement.getElementsByIndexLessThan(index);
    }

    public Elements getElementsByIndexGreaterThan(int index) {
        return originElement.getElementsByIndexGreaterThan(index);
    }

    public Elements getElementsByIndexEquals(int index) {
        return originElement.getElementsByIndexEquals(index);
    }

    public Elements getElementsContainingText(String searchText) {
        return originElement.getElementsContainingText(searchText);
    }

    public Elements getElementsContainingOwnText(String searchText) {
        return originElement.getElementsContainingOwnText(searchText);
    }

    public Elements getElementsMatchingText(Pattern pattern) {
        return originElement.getElementsMatchingText(pattern);
    }

    public Elements getElementsMatchingText(String regex) {
        return originElement.getElementsMatchingText(regex);
    }

    public Elements getElementsMatchingOwnText(Pattern pattern) {
        return originElement.getElementsMatchingOwnText(pattern);
    }

    public Elements getElementsMatchingOwnText(String regex) {
        return originElement.getElementsMatchingOwnText(regex);
    }

    public Elements getAllElements() {
        return originElement.getAllElements();
    }

    public String text() {
        return originElement.text();
    }

    public String ownText() {
        return originElement.ownText();
    }

    public Element text(String text) {
        return originElement.text(text);
    }

    public boolean hasText() {
        return originElement.hasText();
    }

    public String data() {
        return originElement.data();
    }

    public String className() {
        return originElement.className();
    }

    public Set<String> classNames() {
        return originElement.classNames();
    }

    public Element classNames(Set<String> classNames) {
        return originElement.classNames(classNames);
    }

    public boolean hasClass(String className) {
        return originElement.hasClass(className);
    }

    public Element addClass(String className) {
        return originElement.addClass(className);
    }

    public Element removeClass(String className) {
        return originElement.removeClass(className);
    }

    public Element toggleClass(String className) {
        return originElement.toggleClass(className);
    }

    public String val() {
        return originElement.val();
    }

    public Element val(String value) {
        return originElement.val(value);
    }

    public String html() {
        return originElement.html();
    }

    public Element html(String html) {
        return originElement.html(html);
    }

    public String toString() {
        return originElement.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (getClass() != obj.getClass()) {
            return false;
        }

        TestableElementWrapper other = (TestableElementWrapper) obj;
        if (originElement == null) {
            if (other.originElement != null)
                return false;
        } else if (!originElement.outerHtml().equals(other.originElement.outerHtml()))
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        return originElement.hashCode();
    }

    public Element clone() {
        return originElement.clone();
    }

}
