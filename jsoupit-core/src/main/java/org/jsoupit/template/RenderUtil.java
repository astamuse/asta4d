package org.jsoupit.template;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;
import org.jsoupit.template.extnode.ExtNodeConstants;
import org.jsoupit.template.render.GoThroughRenderer;
import org.jsoupit.template.render.Renderer;
import org.jsoupit.template.snippet.SnippetInvokeException;
import org.jsoupit.template.snippet.SnippetInvoker;
import org.jsoupit.template.snippet.SnippetNotResovlableException;
import org.jsoupit.template.transformer.Transformer;

/**
 * 
 * This class is a functions holder which supply the ability of applying
 * rendereres to certain Element.
 * 
 * @author e-ryu
 * 
 */
public class RenderUtil {

    private final static String CurrentRenderingElementKey = RenderUtil.class.getName() + "##-CurrentRenderingElementKey";

    public final static String findAttrFromRenderingElement(String attrName) {
        Context context = Context.getCurrentThreadContext();
        Element elem = context.getData(CurrentRenderingElementKey);
        String value = null;
        while (value == null && elem != null) {
            if (elem.hasAttr(attrName)) {
                value = elem.attr(attrName);
            }
            elem = elem.parent();
        }
        return value;
    }

    public final static void applySnippets(Element elem) throws SnippetNotResovlableException, SnippetInvokeException {
        List<Element> snippetList = new ArrayList<>(elem.select(ExtNodeConstants.SNIPPET_NODE_TAG_SELECTOR +
                "[" +
                ExtNodeConstants.SNIPPET_NODE_ATTR_FINISHED +
                "=false]"));
        String renderDeclaration;
        Renderer renderer;
        Context context = Context.getCurrentThreadContext();
        Configuration conf = context.getConfiguration();
        SnippetInvoker invoker = conf.getSnippetInvoker();
        for (Element element : snippetList) {
            context.setData(CurrentRenderingElementKey, element);
            renderDeclaration = element.attr(ExtNodeConstants.SNIPPET_NODE_ATTR_RENDER);
            renderer = invoker.invoke(renderDeclaration);
            apply(element, renderer);
            element.attr(ExtNodeConstants.SNIPPET_NODE_ATTR_FINISHED, "true");
            context.setData(CurrentRenderingElementKey, null);
        }
        if (snippetList.size() > 0) {
            applySnippets(elem);
        }
    }

    public final static void apply(Node target, Renderer renderer) {
        if (target instanceof Element) {
            apply((Element) target, renderer);
        } else {
            // do nothing
        }
    }

    public final static void apply(Element target, Renderer renderer) {
        Renderer currentRenderer = renderer;
        String selector;
        ListIterator<Element> elemIterator;
        Element elem;
        while (currentRenderer != null) {
            if (currentRenderer instanceof GoThroughRenderer) {
                currentRenderer = currentRenderer.getNext();
                continue;
            }

            selector = currentRenderer.getSelector();
            Elements targets = target.select(selector);
            elemIterator = targets.listIterator();
            while (elemIterator.hasNext()) {
                elem = elemIterator.next();
                List<Transformer<?>> transformerList = currentRenderer.getTransformerList();
                Node resultNode;
                for (Transformer<?> transformer : transformerList) {
                    resultNode = transformer.invoke(elem);
                    elem.before(resultNode);
                }// for
                elem.remove();
            }// while elemIterator
            currentRenderer = currentRenderer.getNext();
        }// while currentRenderer.next
    }

    public final static void applyClearAction(Element target) {
        String removeSnippetSelector = ExtNodeConstants.SNIPPET_NODE_TAG_SELECTOR +
                "[" +
                ExtNodeConstants.SNIPPET_NODE_ATTR_FINISHED +
                "=true]";

        removeJsoupitNodes(target, removeSnippetSelector, true);
        removeJsoupitNodes(target, ExtNodeConstants.CLEAR_NODE_TAG_SELECTOR, false);
        removeJsoupitNodes(target, ExtNodeConstants.GROUP_NODE_TAG_SELECTOR, true);

    }

    public final static void removeJsoupitNodes(Element target, String selector, boolean pullupChildren) {
        Elements removeNodes = target.select(selector);
        Iterator<Element> it = removeNodes.iterator();
        Element rm;
        while (it.hasNext()) {
            rm = it.next();
            if (rm.ownerDocument() == null) {
                continue;
            }
            if (pullupChildren) {
                pullupChildren(rm);
            }
            rm.remove();
        }
    }

    public final static void pullupChildren(Element elem) {
        List<Node> childrenNodes = new ArrayList<>(elem.childNodes());
        for (Node node : childrenNodes) {
            node.remove();
            elem.before(node);
        }
    }
}
