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
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.jsoup.nodes.Element;

import com.astamuse.asta4d.Component;
import com.astamuse.asta4d.Configuration;
import com.astamuse.asta4d.Context;
import com.astamuse.asta4d.data.convertor.DataConvertor;
import com.astamuse.asta4d.render.transformer.ElementSetterTransformer;
import com.astamuse.asta4d.render.transformer.ElementTransformer;
import com.astamuse.asta4d.render.transformer.RenderableTransformer;
import com.astamuse.asta4d.render.transformer.RendererTransformer;
import com.astamuse.asta4d.render.transformer.Transformer;
import com.astamuse.asta4d.render.transformer.TransformerFactory;
import com.astamuse.asta4d.util.collection.ListConvertUtil;
import com.astamuse.asta4d.util.collection.ParallelRowConvertor;
import com.astamuse.asta4d.util.collection.RowConvertor;

/**
 * A renderer is for describing rendering actions.
 * <p>
 * Developers usually do not need to create a Renderer by constructors directly, alternatively a renderer can be created by calling the
 * static {@code create} methods or the {@code add} methods.
 * 
 * 
 * @author e-ryu
 * 
 */
public class Renderer {

    /**
     * This value is for old source compatibility.set it to false will return to the old style action which require a ClearNode to remove
     * node and will also throw NullPointerException if the specified value is null.
     * 
     * We will remove this flag at sometime after we migrated all of our existing sources.
     */
    private final static boolean treatNullAsRemoveNode;
    static {
        String treat = System.getProperty("com.astamuse.asta4d.render.treatNullAsRemoveNode");
        if (treat == null) {
            treatNullAsRemoveNode = true;
        } else {
            treatNullAsRemoveNode = Boolean.parseBoolean(treat);
        }
    }

    private final static boolean saveCallstackInfo;
    static {
        saveCallstackInfo = Configuration.getConfiguration().isSaveCallstackInfoOnRendererCreation();
    }

    private String selector;

    private List<Transformer<?>> transformerList;

    private List<Renderer> chain;

    private String creationSiteInfo = null;

    /**
     * Create a Renderer by given css selector and {@link Transformer}
     * 
     * @param selector
     *            a selector that describes the rendering target element
     * @param transformer
     *            the action that describes how to render the target element
     */
    public Renderer(String selector, Transformer<?> transformer) {
        List<Transformer<?>> list = new ArrayList<>();
        list.add(transformer);
        init(selector, list);
    }

    /**
     * Create a Renderer by given css selector and List of {@link Transformer}. By given a list, the target element will be duplicated to
     * the same count of Transformer list size.
     * 
     * @param selector
     *            a selector that describes the rendering target element
     * @param transformerList
     *            the action list that describes how to render the target element
     */

    public Renderer(String selector, List<Transformer<?>> transformerList) {
        init(selector, transformerList);
    }

    private void init(String selector, List<Transformer<?>> transformerList) {
        this.selector = selector;
        this.transformerList = transformerList;
        chain = new ArrayList<>();
        chain.add(this);

        if (saveCallstackInfo) {
            StackTraceElement[] Stacks = Thread.currentThread().getStackTrace();
            StackTraceElement callSite = null;
            boolean myClsStarted = false;
            for (StackTraceElement stackTraceElement : Stacks) {
                if (stackTraceElement.getClassName().equals(Renderer.class.getName())) {
                    myClsStarted = true;
                    continue;
                } else if (myClsStarted) {
                    callSite = stackTraceElement;
                    break;
                }
            }
            if (callSite != null) {
                creationSiteInfo = callSite.toString();
            }
        }
    }

    public String getSelector() {
        return selector;
    }

    public List<Transformer<?>> getTransformerList() {
        return transformerList;
    }

    @Override
    public String toString() {
        return "\n\"" + selector + "\"#>\n{" + this.transformerList + "}\n\n";
    }

    RendererType getRendererType() {
        return RendererType.COMMON;
    }

    String getCreationSiteInfo() {
        return creationSiteInfo;
    }

    /**
     * Get list of all the renderers hold by the current renderer
     * 
     * @return an unmodifiable list of renderers
     */
    public List<Renderer> asUnmodifiableList() {
        return Collections.unmodifiableList(new ArrayList<>(this.chain));
    }

    /**
     * add a renderer to the current renderer as a list
     * 
     * @param renderer
     *            a renderer
     * @return the parameter renderer for chain calling
     */
    public Renderer add(Renderer renderer) {
        this.chain.addAll(renderer.chain);
        for (Renderer r : renderer.chain) {
            r.chain = this.chain;
        }
        return renderer;
    }

    /**
     * See {@link #add(String, String)}.
     * 
     * @param selector
     *            a css selector
     * @param value
     *            a long value that will be treated as a String
     * @return the created renderer for chain calling
     */
    public Renderer add(String selector, Long value) {
        return add(create(selector, value));
    }

    /**
     * See {@link #add(String, String)}.
     * 
     * @param selector
     *            a css selector
     * @param value
     *            an int value that will be treated as a String
     * @return the created renderer for chain calling
     */
    public Renderer add(String selector, Integer value) {
        return add(create(selector, value));
    }

    /**
     * See {@link #add(String, String)}.
     * 
     * @param selector
     *            a css selector
     * @param value
     *            a boolean value that will be treated as a String
     * @return the created renderer for chain calling
     */
    public Renderer add(String selector, Boolean value) {
        return add(create(selector, value));
    }

    /**
     * Create a renderer for text rendering by given parameter and add it to the current renderer. See {@link #create(String, String)}.
     * 
     * @param selector
     *            a css selector
     * @param value
     *            a String value that will be rendered
     * @return the created renderer for chain calling
     */
    public Renderer add(String selector, String value) {
        return add(create(selector, value));
    }

    /**
     * Create a renderer for given value and add it to the current renderer. See {@link #create(String, Object)}.
     * 
     * @param selector
     *            a css selector
     * @param value
     *            a Object value that will be rendered
     * @return the created renderer for chain calling
     */
    public Renderer add(String selector, Object value) {
        return add(create(selector, value));
    }

    /**
     * Create a renderer for predefined {@link SpecialRenderer}s.
     * 
     * @param selector
     *            a css selector
     * @param specialRenderer
     *            a predefined special renderer
     * @return the created renderer for chain calling
     */
    public Renderer add(String selector, SpecialRenderer specialRenderer) {
        return add(create(selector, specialRenderer));
    }

    /**
     * See {@link #add(String, String, String)}.
     * 
     * @param selector
     *            a css selector
     * @param attr
     *            the attribute name to set
     * @param value
     *            a long value that will be treated as a String value
     * @return the created renderer for chain calling
     */
    public Renderer add(String selector, String attr, Long value) {
        return add(create(selector, attr, value));
    }

    /**
     * See {@link #add(String, String, String)}.
     * 
     * @param selector
     *            a css selector
     * @param attr
     *            the attribute name to set
     * @param value
     *            an int value that will be treated as a String value
     * @return the created renderer for chain calling
     */
    public Renderer add(String selector, String attr, Integer value) {
        return add(create(selector, attr, value));
    }

    /**
     * See {@link #add(String, String, String)}.
     * 
     * @param selector
     *            a css selector
     * @param attr
     *            the attribute name to set
     * @param value
     *            a boolean value that will be treated as a String value
     * @return the created renderer for chain calling
     */
    public Renderer add(String selector, String attr, Boolean value) {
        return add(create(selector, attr, value));
    }

    /**
     * Create a renderer for attribute setting by given parameter and add it to the current renderer. See
     * {@link #create(String, String, String)}.
     * 
     * @param selector
     *            a css selector
     * @param attr
     *            the attribute name to set
     * @param value
     *            a String value that will be treated as the attribute value
     * @return the created renderer for chain calling
     */
    public Renderer add(String selector, String attr, String value) {
        return add(create(selector, attr, value));
    }

    /**
     * Create a renderer for attribute setting by given parameter and add it to the current renderer. See
     * {@link #create(String, String, Object)}.
     * 
     * @param selector
     *            a css selector
     * @param attr
     *            the attribute name to set
     * @param value
     *            a Object value that will be treated as the attribute value
     * @return the created renderer for chain calling
     */
    public Renderer add(String selector, String attr, Object value) {
        return add(create(selector, attr, value));
    }

    /**
     * Create a renderer for element rendering by given parameter and add it to the current renderer. See {@link #create(String, Element)}.
     * 
     * @param selector
     *            a css selector
     * @param elem
     *            a element that to be rendered
     * @return the created renderer for chain calling
     */
    public Renderer add(String selector, Element elem) {
        return add(create(selector, elem));
    }

    /**
     * Create a renderer for {@link Component} rendering by given parameter and add it to the current renderer. See
     * {@link #create(String, Component)}.
     * 
     * @param selector
     *            a css selector
     * @param component
     *            a component that to be rendered
     * @return the created renderer for chain calling
     */
    public Renderer add(String selector, Component component) {
        return add(create(selector, component));
    }

    /**
     * Create a renderer for element setting by given parameter and add it to the current renderer. See
     * {@link #create(String, ElementSetter)}.
     * 
     * @param selector
     *            a css selector
     * @param setter
     *            an ElementSetter
     * @return the created renderer for chain calling
     */
    public Renderer add(String selector, ElementSetter setter) {
        return add(create(selector, setter));
    }

    /**
     * Create a renderer for delayed rendering callback. See {@link #create(String, Renderable)}.
     * 
     * @param selector
     *            a css selector
     * @param Renderable
     *            a callback instance of
     * 
     * @return the created renderer
     */
    public Renderer add(String selector, Renderable renderable) {
        return add(create(selector, renderable));
    }

    /**
     * Create a renderer for recursive renderer rendering by given parameter and add it to the current renderer. See
     * {@link #create(String, Renderer)}.
     * 
     * @param selector
     *            a css selector
     * @param renderer
     *            a renderer
     * @return the created renderer for chain calling
     */
    public Renderer add(String selector, Renderer renderer) {
        return add(create(selector, renderer));
    }

    /**
     * Create a renderer for list rendering by given parameter and add it to the current renderer. See {@link #create(String, List)}.
     * 
     * @param selector
     *            a css selector
     * @param list
     *            a list that can contain all the types that supported by the non-list add methods of Renderer.
     * @return the created renderer for chain calling
     */
    public Renderer add(String selector, Iterable<?> list) {
        return add(create(selector, list));
    }

    /**
     * Create a renderer for list rendering by given parameter with given {@link RowConvertor} and add it to the current renderer. See
     * {@link #create(String, List)}.
     * 
     * @param selector
     * @param list
     * @param convertor
     * @return the created renderer for chain calling
     */
    public <S, T> Renderer add(String selector, Iterable<S> list, RowConvertor<S, T> convertor) {
        return add(create(selector, list, convertor));
    }

    /**
     * Create a renderer for list rendering by given parameter with given {@link DataConvertor} and add it to the current renderer. See
     * {@link #create(String, Iterable, ParallelRowConvertor)}.
     * 
     * @param selector
     * @param list
     * @param convertor
     * @return the created renderer for chain calling
     */
    public <S, T> Renderer add(String selector, Iterable<S> list, ParallelRowConvertor<S, T> convertor) {
        return add(create(selector, list, convertor));
    }

    /**
     * add a {@link DebugRenderer} to the current Renderer and when this renderer is applied, the target element specified by the given
     * selector will be output by logger.
     * 
     * @param selector
     *            a css selector
     * @return the created renderer or the current renderer for chain calling
     */
    public Renderer addDebugger(String logMessage, String selector) {
        return DebugRenderer.logger.isDebugEnabled() ? add(create(selector, new DebugRenderer(logMessage))) : this;
    }

    /**
     * add a {@link DebugRenderer} to the current Renderer and when this renderer is applied, the current rendering element (see
     * {@link Context#setCurrentRenderingElement(Element)}) will be output by logger.
     * 
     * @return the created renderer or the current renderer for chain calling
     */
    public Renderer addDebugger(String logMessage) {
        return DebugRenderer.logger.isDebugEnabled() ? add(new DebugRenderer(logMessage)) : this;
    }

    /**
     * See {@link #create(String, String)}.
     * 
     * @param selector
     *            a css selector
     * @param value
     *            a long value that will be treated as a String
     * @return the created renderer
     */
    public final static Renderer create(String selector, Long value) {
        if (treatNullAsRemoveNode && value == null) {
            return new Renderer(selector, new ElementRemover());
        } else {
            return create(selector, new TextSetter(value));
        }
    }

    /**
     * See {@link #create(String, String)}.
     * 
     * @param selector
     *            a css selector
     * @param value
     *            an int value that will be treated as a String
     * @return the created renderer
     */
    public final static Renderer create(String selector, Integer value) {
        if (treatNullAsRemoveNode && value == null) {
            return new Renderer(selector, new ElementRemover());
        } else {
            return create(selector, new TextSetter(value));
        }
    }

    /**
     * See {@link #create(String, String)}.
     * 
     * @param selector
     *            a css selector
     * @param value
     *            a boolean value that will be treated as a String
     * @return the created renderer
     */
    public final static Renderer create(String selector, Boolean value) {
        if (treatNullAsRemoveNode && value == null) {
            return new Renderer(selector, new ElementRemover());
        } else {
            return create(selector, new TextSetter(value));
        }
    }

    /**
     * Create a renderer for text by given parameter.
     * <p>
     * All child nodes of the target element specified by selector will be emptied and the given String value will be rendered as a single
     * text node of the target element.
     * 
     * @param selector
     *            a css selector
     * @param value
     *            a String value that will be rendered
     * @return the created renderer
     */
    public final static Renderer create(String selector, String value) {
        if (treatNullAsRemoveNode && value == null) {
            return new Renderer(selector, new ElementRemover());
        } else {
            return create(selector, new TextSetter(value));
        }
    }

    /**
     * Create a renderer for given parameter.
     * <p>
     * A special typed renderer will be created by the type of the given value. If there is no coordinate renderer for the type of given
     * value, the value#toString() will be used to retrieve a text for rendering.
     * 
     * @param selector
     *            a css selector
     * @param value
     *            a Object that will be rendered.
     * @return the created renderer
     */
    public final static Renderer create(String selector, Object value) {
        if (treatNullAsRemoveNode && value == null) {
            return new Renderer(selector, new ElementRemover());
        } else {
            return new Renderer(selector, TransformerFactory.generateTransformer(value));
        }
    }

    /**
     * Create a renderer for predefined {@link SpecialRenderer}s.
     * 
     * @param selector
     *            a css selector
     * @param specialRenderer
     *            a predefined special renderer
     * @return the created renderer
     */
    public final static Renderer create(String selector, SpecialRenderer specialRenderer) {
        if (treatNullAsRemoveNode && specialRenderer == null) {
            return new Renderer(selector, new ElementRemover());
        } else {
            return new Renderer(selector, specialRenderer.getTransformer());
        }
    }

    /**
     * See {@link #create(String, String, String)}.
     * 
     * @param selector
     *            a css selector
     * @param attr
     *            the attribute name to set
     * @param value
     *            a long value that will be treated as a String value
     * @return the created renderer
     */
    public final static Renderer create(String selector, String attr, Long value) {
        if (value == null) {
            return create(selector, attr, (String) null);
        } else {
            return create(selector, attr, String.valueOf(value));
        }
    }

    /**
     * See {@link #create(String, String, String)}.
     * 
     * @param selector
     *            a css selector
     * @param attr
     *            the attribute name to set
     * @param value
     *            an int value that will be treated as a String value
     * @return the created renderer
     */
    public final static Renderer create(String selector, String attr, Integer value) {
        if (value == null) {
            return create(selector, attr, (String) null);
        } else {
            return create(selector, attr, String.valueOf(value));
        }
    }

    /**
     * See {@link #create(String, String, String)}.
     * 
     * @param selector
     *            a css selector
     * @param attr
     *            the attribute name to set
     * @param value
     *            a boolean value that will be treated as a String value
     * @return the created renderer
     */
    public final static Renderer create(String selector, String attr, Boolean value) {
        if (value == null) {
            return create(selector, attr, (String) null);
        } else {
            return create(selector, attr, String.valueOf(value));
        }
    }

    /**
     * Create a renderer for attribute setting by given parameter.
     * <p>
     * An additional character of "+" or "-" can be used as a prefix of attribute name. See detail at {@link AttributeSetter}.
     * 
     * @param selector
     *            a css selector
     * @param attr
     *            the attribute name to set
     * @param value
     *            a String value that will be treated as the attribute value
     * @return the created renderer
     */
    public final static Renderer create(String selector, String attr, String value) {
        return create(selector, new AttributeSetter(attr, value));
    }

    /**
     * Create a renderer for attribute setting by given parameter.
     * <p>
     * An additional character of "+" or "-" can be used as a prefix of attribute name. There is also a special logic for an instance with
     * arbitrary type. See detail at {@link AttributeSetter}.
     * 
     * @param selector
     *            a css selector
     * @param attr
     *            the attribute name to set
     * @param value
     *            a Object value that will be treated as the attribute value
     * @return the created renderer
     */
    public final static Renderer create(String selector, String attr, Object value) {
        return create(selector, new AttributeSetter(attr, value));
    }

    /**
     * Create a renderer for element rendering by given parameter.
     * <p>
     * The target element specified by the selector will be completely replaced by the given element.
     * 
     * @param selector
     *            a css selector
     * @param elem
     *            a element that to be rendered
     * @return the created renderer
     */
    public final static Renderer create(String selector, Element elem) {
        if (treatNullAsRemoveNode && elem == null) {
            return new Renderer(selector, new ElementRemover());
        } else {
            return new Renderer(selector, new ElementTransformer(elem));
        }
    }

    /**
     * Create a renderer for {@link Component} rendering by given parameter.
     * <p>
     * The target element specified by the selector will be completely replaced by the result of given {@link Component#toElement()}.
     * 
     * @param selector
     *            a css selector
     * @param component
     *            a component that to be rendered
     * @return the created renderer
     */
    public final static Renderer create(String selector, Component component) {
        if (treatNullAsRemoveNode && component == null) {
            return new Renderer(selector, new ElementRemover());
        } else {
            return new Renderer(selector, new ElementTransformer(component.toElement()));
        }
    }

    /**
     * Create a renderer for element setting by given parameter.
     * <p>
     * The target element specified by the given selector will not be replaced and will be passed to the given {@link ElementSetter} as a
     * parameter.
     * 
     * @param selector
     *            a css selector
     * @param setter
     *            an ElementSetter
     * @return the created renderer
     */
    public final static Renderer create(String selector, ElementSetter setter) {
        if (treatNullAsRemoveNode && setter == null) {
            return new Renderer(selector, new ElementRemover());
        } else {
            return new Renderer(selector, new ElementSetterTransformer(setter));
        }
    }

    /**
     * Create a renderer for delayed rendering callback
     * <p>
     * The target element specified by the given selector will be renderer by the returned value of {@link Renderable#render()} which will
     * not be invoked until the target element is actually requiring the rendering action.
     * 
     * @param selector
     *            a css selector
     * @param Renderable
     *            a callback instance of Renderable
     * @return the created renderer
     */
    public final static Renderer create(String selector, Renderable renderable) {
        if (treatNullAsRemoveNode && renderable == null) {
            return new Renderer(selector, new ElementRemover());
        } else {
            return new Renderer(selector, new RenderableTransformer(renderable));
        }
    }

    /**
     * Create a renderer for recursive renderer rendering by given parameter.
     * <p>
     * The given renderer will be applied to element specified by the given selector.
     * 
     * @param selector
     *            a css selector
     * @param renderer
     *            a renderer
     * @return the created renderer for chain calling
     */
    public final static Renderer create(String selector, Renderer renderer) {
        if (treatNullAsRemoveNode && renderer == null) {
            return new Renderer(selector, new ElementRemover());
        } else {
            return new Renderer(selector, new RendererTransformer(renderer));
        }
    }

    /**
     * This method is a convenience to creating an instance of {@link GoThroughRenderer}
     * 
     * @return a {@link GoThroughRenderer} instance
     */
    public final static Renderer create() {
        return new GoThroughRenderer();
    }

    /**
     * Create a renderer for list rendering by given parameter.
     * <p>
     * The target Element specified by the given selector will be duplicated times as the count of the given list and the contents of the
     * list will be applied to the target Element too.
     * 
     * @param selector
     *            a css selector
     * @param list
     *            a list that can contain all the types supported by the non-list add methods of Renderer
     * @return the created renderer
     */
    public final static Renderer create(String selector, Iterable<?> list) {
        if (treatNullAsRemoveNode && list == null) {
            return new Renderer(selector, new ElementRemover());
        } else {
            List<Transformer<?>> transformerList = new LinkedList<>();
            for (Object obj : list) {
                transformerList.add(TransformerFactory.generateTransformer(obj));
            }
            return new Renderer(selector, transformerList);
        }
    }

    /**
     * Create a renderer for list rendering by given parameter with given {@link RowConvertor}. See {@link #create(String, List)}.
     * 
     * @param selector
     *            a css selector
     * @param list
     *            a list with arbitrary type data
     * @param convertor
     *            a convertor that can convert the arbitrary types of the list data to the types supported by the non-list create methods of
     *            Renderer
     * @return the created renderer
     */
    public final static <S, T> Renderer create(String selector, Iterable<S> list, RowConvertor<S, T> convertor) {
        if (treatNullAsRemoveNode && list == null) {
            return new Renderer(selector, new ElementRemover());
        } else {
            return create(selector, ListConvertUtil.transform(list, convertor));
        }
    }

    /**
     * Create a renderer for list rendering by given parameter with given {@link ParallelRowConvertor}. This method will not block the
     * current thread and will return immediately.
     * 
     * See {@link #create(String, List)}.
     * 
     * @param selector
     *            a css selector
     * @param list
     *            a list with arbitrary type data
     * @param convertor
     *            a convertor that can convert the arbitrary types of the list data to the types supported by the non-list create methods of
     *            Renderer
     * @return the created renderer
     */
    public final static <S, T> Renderer create(String selector, Iterable<S> list, final ParallelRowConvertor<S, T> convertor) {
        if (treatNullAsRemoveNode && list == null) {
            return new Renderer(selector, new ElementRemover());
        } else {
            if (Configuration.getConfiguration().isBlockParallelListRendering()) {
                return create(selector, ListConvertUtil.transform(list, convertor));
            } else {
                return create(selector, ListConvertUtil.transformToFuture(list, convertor));
            }
        }
    }

    // Render action control

    /**
     * 
     * @return a renderer reference for chain calling
     * 
     * @see {@link RenderActionStyle#DISABLE_MISSING_SELECTOR_WARNING}
     * 
     */
    public Renderer disableMissingSelectorWarning() {
        return this.add(new RenderActionRenderer(RenderActionStyle.DISABLE_MISSING_SELECTOR_WARNING));
    }

    /**
     * 
     * @return a renderer reference for chain calling
     * 
     * @see {@link RenderActionStyle#ENABLE_MISSING_SELECTOR_WARNING}
     * 
     */
    public Renderer enableMissingSelectorWarning() {
        return this.add(new RenderActionRenderer(RenderActionStyle.ENABLE_MISSING_SELECTOR_WARNING));
    }

}
