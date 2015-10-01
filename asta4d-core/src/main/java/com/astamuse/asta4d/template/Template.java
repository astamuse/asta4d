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

package com.astamuse.asta4d.template;

import java.io.IOException;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.astamuse.asta4d.Configuration;
import com.astamuse.asta4d.extnode.ExtNodeConstants;
import com.astamuse.asta4d.util.ElementUtil;
import com.astamuse.asta4d.util.IdGenerator;
import com.astamuse.asta4d.util.SelectorUtil;

public class Template {

    private String path;

    private Document doc = null;

    /**
     * 
     * @param path
     *            the actual template path
     * 
     * @param input
     * @throws IOException
     */
    public Template(String path, Document doc) throws TemplateException, TemplateNotFoundException {
        this.path = path;
        this.doc = doc;
        initDocument();
    }

    private void initDocument() throws TemplateException, TemplateNotFoundException {
        clearCommentNode();
        processExtension();
        TemplateUtil.regulateElement(path, doc);
    }

    private void clearCommentNode() throws TemplateException {
        String commentSelector = SelectorUtil.tag(ExtNodeConstants.COMMENT_NODE_TAG);
        ElementUtil.removeNodesBySelector(doc, commentSelector, false);
    }

    private void processExtension() throws TemplateException, TemplateNotFoundException {
        Element extension = doc.select(ExtNodeConstants.EXTENSION_NODE_TAG_SELECTOR).first();
        if (extension != null) {
            String parentPath = extension.attr(ExtNodeConstants.EXTENSION_NODE_ATTR_PARENT);
            if (parentPath == null || parentPath.isEmpty()) {
                throw new TemplateException(String.format("You must specify the parent of an extension (%s).", this.path));
            }
            Configuration conf = Configuration.getConfiguration();
            Template parent = conf.getTemplateResolver().findTemplate(parentPath);
            Document parentDoc = parent.getDocumentClone();
            TemplateUtil.mergeBlock(parentDoc, extension);

            doc = parentDoc;
        }

    }

    public String getPath() {
        return path;
    }

    public Document getDocumentClone() {
        Document newDoc = doc.clone();
        newDoc.attr(ExtNodeConstants.ATTR_DOC_REF, "doc-" + IdGenerator.createId());
        return newDoc;
    }
}
