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

    private Template template;

    private Element renderedElement;

    public Component(String path, AttributesRequire attrs) throws Exception {
        Configuration conf = Context.getCurrentThreadContext().getConfiguration();
        TemplateResolver templateResolver = conf.getTemplateResolver();
        template = templateResolver.findTemplate(path);
        renderedElement = renderTemplate(template, attrs);
    }

    public Component(String path) throws Exception {
        this(path, null);
    }

    protected Element renderTemplate(Template template, AttributesRequire attrs) throws Exception {
        Document doc = template.getDocumentClone();

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
        Element elem = toElement();
        RenderUtil.applyMessages(elem);
        RenderUtil.applyClearAction(elem, true);
        return elem.html();
    }

    public String toString() {
        return toHtml();
    }

}
