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

package com.astamuse.asta4d.render;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
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
import com.astamuse.asta4d.render.concurrent.ConcurrentRenderHelper;
import com.astamuse.asta4d.render.concurrent.FutureRendererHolder;
import com.astamuse.asta4d.render.transformer.RendererTransformer;
import com.astamuse.asta4d.render.transformer.Transformer;
import com.astamuse.asta4d.snippet.SnippetInvokeException;
import com.astamuse.asta4d.snippet.SnippetInvoker;
import com.astamuse.asta4d.snippet.SnippetNotResovlableException;
import com.astamuse.asta4d.template.TemplateException;
import com.astamuse.asta4d.template.TemplateUtil;
import com.astamuse.asta4d.util.ElementUtil;
import com.astamuse.asta4d.util.SelectorUtil;
import com.astamuse.asta4d.util.i18n.LocalizeUtil;
import com.astamuse.asta4d.util.i18n.ParamMapResourceBundleHelper;

/**
 * 
 * This class is a functions holder which supply the ability of applying rendereres to certain Element.
 * 
 * @author e-ryu
 * 
 */
public class RenderUtil {

    public static final String PSEUDO_ROOT_SELECTOR = ":root";

    private final static Logger logger = LoggerFactory.getLogger(RenderUtil.class);

    private final static List<String> EXCLUDE_ATTR_NAME_LIST = new ArrayList<>();

    static {
        EXCLUDE_ATTR_NAME_LIST.add(ExtNodeConstants.MSG_NODE_ATTR_KEY);
        EXCLUDE_ATTR_NAME_LIST.add(ExtNodeConstants.MSG_NODE_ATTR_LOCALE);
        EXCLUDE_ATTR_NAME_LIST.add(ExtNodeConstants.MSG_NODE_ATTR_EXTERNALIZE);
    }

    /**
     * Find out all the snippet in the passed Document and execute them. The Containing embed tag of the passed Document will be exactly
     * mixed in here too. <br>
     * Recursively contained snippets will be executed from outside to inside, thus the inner snippets will not be executed until all of
     * their outer snippets are finished. Also, the dynamically created snippets and embed tags will comply with this rule too.
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

        applyClearAction(doc, false);

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
        Configuration conf = Configuration.getConfiguration();
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
                    // the hosting element of this faked snippet has been removed by outer a snippet
                    if (renderTarget == null) {
                        element.attr(ExtNodeConstants.SNIPPET_NODE_ATTR_STATUS, ExtNodeConstants.SNIPPET_NODE_ATTR_STATUS_FINISHED);
                        continue;
                    }
                } else {
                    renderTarget = element;
                }
                context.setCurrentRenderingElement(renderTarget);
                renderDeclaration = element.attr(ExtNodeConstants.SNIPPET_NODE_ATTR_RENDER);
                refId = element.attr(ExtNodeConstants.ATTR_SNIPPET_REF);
                try {
                    if (element.hasAttr(ExtNodeConstants.SNIPPET_NODE_ATTR_PARALLEL)) {
                        ConcurrentRenderHelper crHelper = ConcurrentRenderHelper.getInstance(context, doc);
                        final Context newContext = context.clone();
                        final String declaration = renderDeclaration;
                        crHelper.submitWithContext(newContext, declaration, refId, new Callable<Renderer>() {
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
                } catch (SnippetNotResovlableException | SnippetInvokeException e) {
                    throw e;
                } catch (Exception e) {
                    SnippetInvokeException se = new SnippetInvokeException("Error occured when executing rendering on [" +
                            renderDeclaration + "]", e);
                    throw se;
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
                embedNodeListCount--;
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
            ConcurrentRenderHelper crHelper = ConcurrentRenderHelper.getInstance(context, doc);
            String delcaration = null;
            if (crHelper.hasUnCompletedTask()) {
                delcaration = null;
                try {
                    FutureRendererHolder holder = crHelper.take();
                    delcaration = holder.getRenderDeclaration();
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
                    throw new SnippetInvokeException("Concurrent snippet invocation failed" +
                            (delcaration == null ? "" : " on [" + delcaration + "]"), e);
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

        RenderAction renderAction = new RenderAction();

        apply(target, rendererList, renderAction, 0, count);
    }

    // TODO since this method is called recursively, we need do a test to find
    // out the threshold of render list size that will cause a
    // StackOverflowError.
    private final static void apply(Element target, List<Renderer> rendererList, RenderAction renderAction, int startIndex, int count) {

        // The renderer list have to be applied recursively because the
        // transformer will always return a new Element clone.

        if (startIndex >= count) {
            return;
        }

        final Renderer currentRenderer = rendererList.get(startIndex);

        RendererType rendererType = currentRenderer.getRendererType();

        switch (rendererType) {
        case GO_THROUGH:
            apply(target, rendererList, renderAction, startIndex + 1, count);
            return;
        case DEBUG:
            currentRenderer.getTransformerList().get(0).invoke(target);
            apply(target, rendererList, renderAction, startIndex + 1, count);
            return;
        case RENDER_ACTION:
            ((RenderActionRenderer) currentRenderer).getStyle().apply(renderAction);
            apply(target, rendererList, renderAction, startIndex + 1, count);
            return;
        default:
            // do nothing
            break;
        }

        String selector = currentRenderer.getSelector();
        List<Transformer<?>> transformerList = currentRenderer.getTransformerList();

        List<Element> elemList;
        if (PSEUDO_ROOT_SELECTOR.equals(selector)) {
            elemList = new LinkedList<Element>();
            elemList.add(target);
        } else {
            elemList = new ArrayList<>(target.select(selector));
        }

        if (elemList.isEmpty()) {
            if (rendererType == RendererType.ELEMENT_NOT_FOUND_HANDLER) {
                elemList.add(target);
                transformerList.clear();
                transformerList.add(new RendererTransformer(((ElementNotFoundHandler) currentRenderer).alternativeRenderer()));
            } else if (renderAction.isOutputMissingSelectorWarning()) {
                String creationInfo = currentRenderer.getCreationSiteInfo();
                if (creationInfo == null) {
                    creationInfo = "";
                } else {
                    creationInfo = " at [ " + creationInfo + " ]";
                }
                logger.warn(
                        "There is no element found for selector [{}]{}, if it is deserved, try Renderer#disableMissingSelectorWarning() "
                                + "to disable this message and Renderer#enableMissingSelectorWarning could enable this warning again in "
                                + "your renderer chain", selector, creationInfo);
                apply(target, rendererList, renderAction, startIndex + 1, count);
                return;
            }

        } else {
            if (rendererType == RendererType.ELEMENT_NOT_FOUND_HANDLER) {
                apply(target, rendererList, renderAction, startIndex + 1, count);
                return;
            }
        }

        Element delayedElement = null;
        Element resultNode;
        // TODO we suppose that the element is listed as the order from parent
        // to children, so we reverse it. Perhaps we need a real order process
        // to ensure the wanted order.
        Collections.reverse(elemList);
        boolean renderForRoot;
        for (Element elem : elemList) {
            renderForRoot = PSEUDO_ROOT_SELECTOR.equals(selector) || rendererType == RendererType.ELEMENT_NOT_FOUND_HANDLER;
            if (!renderForRoot) {
                // faked group node will be not applied by renderers(only when the current selector is not the pseudo :root)
                if (elem.tagName().equals(ExtNodeConstants.GROUP_NODE_TAG) &&
                        ExtNodeConstants.GROUP_NODE_ATTR_TYPE_FAKE.equals(elem.attr(ExtNodeConstants.GROUP_NODE_ATTR_TYPE))) {
                    continue;
                }
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
            apply(target, rendererList, renderAction, startIndex + 1, count);
        } else {
            if (rendererType == RendererType.ELEMENT_NOT_FOUND_HANDLER && delayedElement instanceof Document) {
                delayedElement = delayedElement.child(0);
            }
            for (Transformer<?> transformer : transformerList) {
                resultNode = transformer.invoke(delayedElement);
                delayedElement.before(resultNode);
                apply(resultNode, rendererList, renderAction, startIndex + 1, count);
            }// for transformer
            delayedElement.remove();
        }

    }

    /**
     * Clear the redundant elements which are usually created by snippet/renderer applying.If the forFinalClean is true, all the finished
     * snippet tags will be removed too.
     * 
     * @param target
     * @param forFinalClean
     */
    public final static void applyClearAction(Element target, boolean forFinalClean) {
        String fakeGroup = SelectorUtil.attr(ExtNodeConstants.GROUP_NODE_TAG_SELECTOR, ExtNodeConstants.GROUP_NODE_ATTR_TYPE,
                ExtNodeConstants.GROUP_NODE_ATTR_TYPE_FAKE);
        ElementUtil.removeNodesBySelector(target, fakeGroup, true);

        String clearGroup = SelectorUtil.attr(ExtNodeConstants.GROUP_NODE_TAG_SELECTOR, ExtNodeConstants.ATTR_CLEAR, null);
        ElementUtil.removeNodesBySelector(target, clearGroup, false);

        ElementUtil.removeNodesBySelector(target, SelectorUtil.attr(ExtNodeConstants.ATTR_CLEAR_WITH_NS), false);

        if (forFinalClean) {
            String removeSnippetSelector = SelectorUtil.attr(ExtNodeConstants.SNIPPET_NODE_TAG_SELECTOR,
                    ExtNodeConstants.SNIPPET_NODE_ATTR_STATUS, ExtNodeConstants.SNIPPET_NODE_ATTR_STATUS_FINISHED);
            // TODO check if there are unfinished snippet left.
            ElementUtil.removeNodesBySelector(target, removeSnippetSelector, true);
            ElementUtil.removeNodesBySelector(target, ExtNodeConstants.BLOCK_NODE_TAG_SELECTOR, true);
            ElementUtil.removeNodesBySelector(target, ExtNodeConstants.GROUP_NODE_TAG_SELECTOR, true);
        }

    }

    public final static void applyMessages(Element target) {
        String selector = SelectorUtil.tag(ExtNodeConstants.MSG_NODE_TAG);
        List<Element> msgElems = target.select(selector);
        for (Element msgElem : msgElems) {
            Attributes attributes = msgElem.attributes();
            String key = attributes.get(ExtNodeConstants.MSG_NODE_ATTR_KEY);
            List<String> externalizeParamKeys = getExternalizeParamKeys(attributes);
            String defaultMsg = ExtNodeConstants.MSG_NODE_ATTRVALUE_HTML_PREFIX + msgElem.html();

            // TODO cache localed helper instance
            ParamMapResourceBundleHelper helper = null;
            if (attributes.hasKey(ExtNodeConstants.MSG_NODE_ATTR_LOCALE)) {
                helper = new ParamMapResourceBundleHelper(LocalizeUtil.getLocale(attributes.get(ExtNodeConstants.MSG_NODE_ATTR_LOCALE)));
            } else {
                helper = new ParamMapResourceBundleHelper();
            }

            Map<String, Object> paramMap = getMessageParams(attributes, helper, key, externalizeParamKeys);
            String text;
            text = helper.getMessageWithDefault(key, defaultMsg, paramMap);

            Node node;
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

    private static Map<String, Object> getMessageParams(Attributes attributes, ParamMapResourceBundleHelper helper, String key,
            List<String> externalizeParamKeys) {
        List<String> excludeAttrNameList = EXCLUDE_ATTR_NAME_LIST;
        Map<String, Object> paramMap = new HashMap<>();
        for (Attribute attribute : attributes) {
            String attrKey = attribute.getKey();
            if (excludeAttrNameList.contains(attrKey)) {
                continue;
            }
            String value = attribute.getValue();
            if (externalizeParamKeys.contains(attrKey)) {
                paramMap.put(attrKey, helper.getExternalParamValue(key, value));
            } else {
                paramMap.put(attrKey, value);
            }
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
