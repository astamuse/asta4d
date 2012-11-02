package com.astamuse.asta4d.render;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.astamuse.asta4d.Configuration;
import com.astamuse.asta4d.Context;
import com.astamuse.asta4d.extnode.ExtNodeConstants;
import com.astamuse.asta4d.i18n.InvalidMessageException;
import com.astamuse.asta4d.i18n.MessagesUtil;
import com.astamuse.asta4d.i18n.ResourceBundleManager;
import com.astamuse.asta4d.render.concurrent.ConcurrentRenderHelper;
import com.astamuse.asta4d.render.concurrent.FutureRendererHolder;
import com.astamuse.asta4d.render.transformer.Transformer;
import com.astamuse.asta4d.snippet.SnippetInvokeException;
import com.astamuse.asta4d.snippet.SnippetInvoker;
import com.astamuse.asta4d.snippet.SnippetNotResovlableException;
import com.astamuse.asta4d.template.TemplateException;
import com.astamuse.asta4d.template.TemplateUtil;
import com.astamuse.asta4d.util.ElementUtil;
import com.astamuse.asta4d.util.LocalizeUtil;
import com.astamuse.asta4d.util.SelectorUtil;

/**
 * 
 * This class is a functions holder which supply the ability of applying
 * rendereres to certain Element.
 * 
 * @author e-ryu
 * 
 */
public class RenderUtil {

    private final static Logger LOGGER = LoggerFactory.getLogger(RenderUtil.class);

    /**
     * Find out all the snippet in the passed Document and execute them. The
     * Containing embed tag of the passed Document will be exactly mixed in here
     * too. <br>
     * Recursively contained snippets will be executed from outside to inside,
     * thus the inner snippets will not be executed until all of their outer
     * snippets are finished. Also, the dynamically created snippets and embed
     * tags will comply with this rule too.
     * 
     * @param doc
     *            the Document to apply snippets
     * @throws SnippetNotResovlableException
     * @throws SnippetInvokeException
     * @throws TemplateException
     */
    public final static void applySnippets(Document doc) throws SnippetNotResovlableException, SnippetInvokeException, TemplateException {
        if (doc == null) {
            return;
        }
        // retrieve ready snippets
        String selector = SelectorUtil.attr(ExtNodeConstants.SNIPPET_NODE_TAG_SELECTOR, ExtNodeConstants.SNIPPET_NODE_ATTR_STATUS,
                ExtNodeConstants.SNIPPET_NODE_ATTR_STATUS_READY);
        List<Element> snippetList = new ArrayList<>(doc.select(selector));
        int readySnippetCount = snippetList.size();
        int blockedSnippetCount = 0;
        for (int i = readySnippetCount - 1; i >= 0; i--) {
            // if parent snippet has not been executed, the current snippet will
            // not be executed too.
            if (isBlockedByParentSnippet(doc, snippetList.get(i))) {
                snippetList.remove(i);
                blockedSnippetCount++;
            }
        }
        readySnippetCount = readySnippetCount - blockedSnippetCount;

        String renderDeclaration;
        Renderer renderer;
        Context context = Context.getCurrentThreadContext();
        Configuration conf = context.getConfiguration();
        final SnippetInvoker invoker = conf.getSnippetInvoker();

        String refId;
        Element renderTarget;
        for (Element element : snippetList) {
            if (!conf.isSkipSnippetExecution()) {
                // for a faked snippet node which is created by template
                // analyzing process, the render target element should be its
                // child.
                if (element.attr(ExtNodeConstants.SNIPPET_NODE_ATTR_TYPE).equals(ExtNodeConstants.SNIPPET_NODE_ATTR_TYPE_FAKE)) {
                    renderTarget = element.children().first();
                } else {
                    renderTarget = element;
                }
                context.setCurrentRenderingElement(renderTarget);
                renderDeclaration = element.attr(ExtNodeConstants.SNIPPET_NODE_ATTR_RENDER);
                refId = element.attr(ExtNodeConstants.ATTR_SNIPPET_REF);
                if (element.hasAttr(ExtNodeConstants.SNIPPET_NODE_ATTR_PARALLEL)) {
                    ConcurrentRenderHelper crHelper = ConcurrentRenderHelper.getInstance(context);
                    final Context newContext = context.clone();
                    final String declaration = renderDeclaration;
                    crHelper.submitWithContext(newContext, refId, new Callable<Renderer>() {
                        @Override
                        public Renderer call() throws Exception {
                            return invoker.invoke(declaration);
                        }
                    });
                    element.attr(ExtNodeConstants.SNIPPET_NODE_ATTR_STATUS, ExtNodeConstants.SNIPPET_NODE_ATTR_STATUS_WAITING);
                } else {
                    renderer = invoker.invoke(renderDeclaration);
                    applySnippetResultToElement(doc, refId, element, renderTarget, renderer);
                }
                context.setCurrentRenderingElement(null);
            } else {// if skip snippet
                element.attr(ExtNodeConstants.SNIPPET_NODE_ATTR_STATUS, ExtNodeConstants.SNIPPET_NODE_ATTR_STATUS_FINISHED);
            }

        }

        // load embed nodes which blocking parents has finished
        List<Element> embedNodeList = doc.select(ExtNodeConstants.EMBED_NODE_TAG_SELECTOR);
        int embedNodeListCount = embedNodeList.size();
        Iterator<Element> embedNodeIterator = embedNodeList.iterator();
        Element embed;
        Element embedContent;
        while (embedNodeIterator.hasNext()) {
            embed = embedNodeIterator.next();
            if (isBlockedByParentSnippet(doc, embed)) {
                continue;
            }
            embedContent = TemplateUtil.getEmbedNodeContent(embed);
            TemplateUtil.mergeBlock(doc, embedContent);
            embed.before(embedContent);
            embed.remove();
        }

        if ((readySnippetCount + embedNodeListCount) > 0) {
            TemplateUtil.regulateElement(doc);
            applySnippets(doc);
        } else {
            ConcurrentRenderHelper crHelper = ConcurrentRenderHelper.getInstance(context);
            if (crHelper.hasUnCompletedTask()) {
                try {
                    FutureRendererHolder holder = crHelper.take();
                    String ref = holder.getSnippetRefId();
                    String reSelector = SelectorUtil.attr(ExtNodeConstants.SNIPPET_NODE_TAG_SELECTOR, ExtNodeConstants.ATTR_SNIPPET_REF,
                            ref);
                    Element element = doc.select(reSelector).get(0);// must have
                    Element target;
                    if (element.attr(ExtNodeConstants.SNIPPET_NODE_ATTR_TYPE).equals(ExtNodeConstants.SNIPPET_NODE_ATTR_TYPE_FAKE)) {
                        target = element.children().first();
                    } else {
                        target = element;
                    }
                    applySnippetResultToElement(doc, ref, element, target, holder.getRenderer());
                    applySnippets(doc);
                } catch (InterruptedException | ExecutionException e) {
                    throw new SnippetInvokeException("Concurrent snippet invocation failed.", e);
                }
            }
        }
    }

    private final static void applySnippetResultToElement(Document doc, String snippetRefId, Element snippetElement, Element renderTarget,
            Renderer renderer) {
        apply(renderTarget, renderer);
        if (snippetElement.ownerDocument() == null) {
            // it means this snippet element is replaced by a
            // element completely
            String reSelector = SelectorUtil.attr(ExtNodeConstants.SNIPPET_NODE_TAG_SELECTOR, ExtNodeConstants.ATTR_SNIPPET_REF,
                    snippetRefId);
            Elements elems = doc.select(reSelector);
            if (elems.size() > 0) {
                snippetElement = elems.get(0);
            } else {
                snippetElement = null;
            }
        }
        if (snippetElement != null) {
            snippetElement.attr(ExtNodeConstants.SNIPPET_NODE_ATTR_STATUS, ExtNodeConstants.SNIPPET_NODE_ATTR_STATUS_FINISHED);
        }
    }

    private final static boolean isBlockedByParentSnippet(Document doc, Element elem) {
        boolean isBlocked;
        String blockingId = elem.attr(ExtNodeConstants.SNIPPET_NODE_ATTR_BLOCK);
        if (blockingId.isEmpty()) {
            // empty block id means there is no parent snippet that need to be
            // aware. if the original block is from a embed template, it means
            // that all of the parent snippets have been finished or this
            // element would not be imported now.
            isBlocked = false;
        } else {
            String parentSelector = SelectorUtil.attr(ExtNodeConstants.SNIPPET_NODE_TAG_SELECTOR, ExtNodeConstants.ATTR_SNIPPET_REF,
                    blockingId);
            Elements parentSnippetSearch = doc.select(parentSelector);
            if (parentSnippetSearch.isEmpty()) {
                isBlocked = false;
            } else {
                Element parentSnippet = parentSnippetSearch.first();
                if (parentSnippet.attr(ExtNodeConstants.SNIPPET_NODE_ATTR_STATUS)
                        .equals(ExtNodeConstants.SNIPPET_NODE_ATTR_STATUS_FINISHED)) {
                    isBlocked = false;
                } else {
                    isBlocked = true;
                }
            }
        }
        return isBlocked;
    }

    /**
     * Apply given renderer to the given element.
     * 
     * @param target
     *            applying target element
     * @param renderer
     *            a renderer for applying
     */
    public final static void apply(Element target, Renderer renderer) {
        List<Renderer> rendererList = renderer.asUnmodifiableList();
        int count = rendererList.size();
        if (count == 0) {
            return;
        }
        applyClearAction(target, false);
        apply(target, rendererList, 0, count);
    }

    // TODO since this method is called recursively, we need do a test to find
    // out the threshold of render list size that will cause a
    // StackOverflowError.
    private final static void apply(Element target, List<Renderer> rendererList, int startIndex, int count) {

        // The renderer list have to be applied recursively because the
        // transformer will always return a new Element clone.

        if (startIndex >= count) {
            return;
        }

        Renderer currentRenderer = rendererList.get(startIndex);

        if (currentRenderer instanceof GoThroughRenderer) {
            apply(target, rendererList, startIndex + 1, count);
            return;
        }

        String selector = currentRenderer.getSelector();

        if (currentRenderer instanceof DebugRenderer) {
            currentRenderer.getTransformerList().get(0).invoke(target);
            apply(target, rendererList, startIndex + 1, count);
            return;
        }

        List<Element> elemList = new ArrayList<>(target.select(selector));
        List<Transformer<?>> transformerList = currentRenderer.getTransformerList();

        Element delayedElement = null;
        Element resultNode;
        // TODO we suppose that the element is listed as the order from parent
        // to children, so we reverse it. Perhaps we need a real order process
        // to ensure the wanted order.
        Collections.reverse(elemList);
        for (Element elem : elemList) {
            if (elem.tagName().equals(ExtNodeConstants.GROUP_NODE_TAG)) {
                continue;
            }
            if (elem == target) {
                delayedElement = elem;
                continue;
            }
            for (Transformer<?> transformer : transformerList) {
                resultNode = transformer.invoke(elem);
                elem.before(resultNode);
            }// for transformer
            elem.remove();
        }// for element

        // if the root element is one of the process targets, we can not apply
        // the left renderers to original element because it will be replaced by
        // a new element even it is not necessary (that is how Transformer
        // works).
        if (delayedElement == null) {
            apply(target, rendererList, startIndex + 1, count);
        } else {
            for (Transformer<?> transformer : transformerList) {
                resultNode = transformer.invoke(delayedElement);
                delayedElement.before(resultNode);
                apply(resultNode, rendererList, startIndex + 1, count);
            }// for transformer
            delayedElement.remove();
        }

    }

    /**
     * Clear the redundant elements which are usually created by
     * snippet/renderer applying.If the forFinalClean is true, all the finished
     * snippet tags will be removed too.
     * 
     * @param target
     * @param forFinalClean
     */
    public final static void applyClearAction(Element target, boolean forFinalClean) {
        if (forFinalClean) {
            String removeSnippetSelector = SelectorUtil.attr(ExtNodeConstants.SNIPPET_NODE_TAG_SELECTOR,
                    ExtNodeConstants.SNIPPET_NODE_ATTR_STATUS, ExtNodeConstants.SNIPPET_NODE_ATTR_STATUS_FINISHED);
            // TODO check if there are unfinished snippet left.
            ElementUtil.removeNodesBySelector(target, removeSnippetSelector, true);
            ElementUtil.removeNodesBySelector(target, ExtNodeConstants.BLOCK_NODE_TAG_SELECTOR, true);

        }

        ElementUtil.removeNodesBySelector(target,
                SelectorUtil.attr(ExtNodeConstants.GROUP_NODE_TAG_SELECTOR, ExtNodeConstants.ATTR_CLEAR, null), false);
        ElementUtil.removeNodesBySelector(target, SelectorUtil.attr(ExtNodeConstants.ATTR_CLEAR_WITH_NS), false);
        ElementUtil.removeNodesBySelector(target, ExtNodeConstants.GROUP_NODE_TAG_SELECTOR, true);

    }

    public final static void applyMessages(Element target) throws InvalidMessageException {
        ResourceBundleManager rbManager = Context.getCurrentThreadContext().getConfiguration().getResourceBundleManager();
        String selector = SelectorUtil.tag(ExtNodeConstants.MSG_NODE_TAG);
        List<Element> msgElems = target.select(selector);
        for (Element msgElem : msgElems) {
            Attributes attributes = msgElem.attributes();
            if (!attributes.hasKey(ExtNodeConstants.MSG_NODE_ATTR_KEY)) {
                throw new InvalidMessageException(ExtNodeConstants.MSG_NODE_TAG + " tag must have key attribute.");
            }
            String key = attributes.get(ExtNodeConstants.MSG_NODE_ATTR_KEY);
            String localeStr = null;
            if (attributes.hasKey(ExtNodeConstants.MSG_NODE_ATTR_LOCALE)) {
                localeStr = attributes.get(ExtNodeConstants.MSG_NODE_ATTR_LOCALE);
            }
            Map<String, String> paramMap = getMessageParams(attributes);
            List<String> externalizeParamKeys = getExternalizeParamKeys(attributes);
            String text;
            Node node = null;
            try {
                text = rbManager.getString(LocalizeUtil.getLocale(localeStr), key, paramMap, externalizeParamKeys);
            } catch (InvalidMessageException e) {
                LOGGER.warn("failed to get the message. key=" + key, e);
                text = '!' + key + '!';
            }
            if (text.startsWith(ExtNodeConstants.MSG_NODE_ATTRVALUE_TEXT_PREFIX)) {
                node = ElementUtil.text(text.substring(ExtNodeConstants.MSG_NODE_ATTRVALUE_TEXT_PREFIX.length()));
            } else if (text.startsWith(ExtNodeConstants.MSG_NODE_ATTRVALUE_HTML_PREFIX)) {
                node = ElementUtil.parseAsSingle(text.substring(ExtNodeConstants.MSG_NODE_ATTRVALUE_HTML_PREFIX.length()));
            } else {
                node = ElementUtil.text(text);
            }
            msgElem.replaceWith(node);
        }
    }

    private static Map<String, String> getMessageParams(Attributes attributes) {
        List<String> excludeAttrNameList = MessagesUtil.getExcludeAttrNameList();
        Map<String, String> paramMap = new HashMap<>();
        for (Attribute attribute : attributes) {
            String key = attribute.getKey();
            if (excludeAttrNameList.contains(key)) {
                continue;
            }
            paramMap.put(key, attribute.getValue());
        }
        return paramMap;
    }

    private static List<String> getExternalizeParamKeys(Attributes attributes) {
        if (attributes.hasKey(ExtNodeConstants.MSG_NODE_ATTR_EXTERNALIZE)) {
            String externalizeParamKeys = attributes.get(ExtNodeConstants.MSG_NODE_ATTR_EXTERNALIZE);
            return Arrays.asList(externalizeParamKeys.split(","));
        } else {
            return Collections.emptyList();
        }
    }
}
