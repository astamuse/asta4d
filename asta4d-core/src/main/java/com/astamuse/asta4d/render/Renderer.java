package com.astamuse.asta4d.render;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import org.jsoup.nodes.Element;

import com.astamuse.asta4d.Context;
import com.astamuse.asta4d.concurrent.ParallelDataConvertor;
import com.astamuse.asta4d.data.DataConvertor;
import com.astamuse.asta4d.render.transformer.ElementSetterTransformer;
import com.astamuse.asta4d.render.transformer.ElementTransformer;
import com.astamuse.asta4d.render.transformer.RendererTransformer;
import com.astamuse.asta4d.render.transformer.Transformer;
import com.astamuse.asta4d.render.transformer.TransformerFactory;

/**
 * A renderer is for describing rendering actions.
 * <p>
 * Developers usually do not need to create a Renderer by constructors directly,
 * alternatively a renderer can be created by calling the static {@code create}
 * methods or the {@code add} methods.
 * 
 * 
 * @author e-ryu
 * 
 */
public class Renderer {

    private String selector;

    private List<Transformer<?>> transformerList;

    private List<Renderer> chain;

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
     * Create a Renderer by given css selector and List of {@link Transformer}.
     * By given a list, the target element will be duplicated to the same count
     * of Transformer list size.
     * 
     * @param selector
     *            a selector that describes the rendering target element
     * @param transformerList
     *            the action list that describes how to render the target
     *            element
     */

    public Renderer(String selector, List<Transformer<?>> transformerList) {
        init(selector, transformerList);
    }

    private void init(String selector, List<Transformer<?>> transformerList) {
        this.selector = selector;
        this.transformerList = transformerList;
        chain = new ArrayList<>();
        chain.add(this);
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
    public Renderer add(String selector, long value) {
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
    public Renderer add(String selector, int value) {
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
    public Renderer add(String selector, boolean value) {
        return add(create(selector, value));
    }

    /**
     * Create a renderer for text rendering by given parameter and add it to the
     * current renderer. See {@link #create(String, String)}.
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
    public Renderer add(String selector, String attr, long value) {
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
    public Renderer add(String selector, String attr, int value) {
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
    public Renderer add(String selector, String attr, boolean value) {
        return add(create(selector, attr, value));
    }

    /**
     * Create a renderer for attribute setting by given parameter and add it to
     * the current renderer. See {@link #create(String, String, String)}.
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
     * Create a renderer for attribute setting by given parameter and add it to
     * the current renderer. See {@link #create(String, String, Object)}.
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
     * Create a renderer for element rendering by given parameter and add it to
     * the current renderer. See {@link #create(String, Element)}.
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
     * Create a renderer for element setting by given parameter and add it to
     * the current renderer. See {@link #create(String, ElementSetter)}.
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
     * Create a renderer for recursive renderer rendering by given parameter and
     * add it to the current renderer. See {@link #create(String, Renderer)}.
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
     * Create a renderer for list rendering by given parameter and add it to the
     * current renderer. See {@link #create(String, List)}.
     * 
     * @param selector
     *            a css selector
     * @param list
     *            a list that can contain all the types that supported by the
     *            non-list add methods of Renderer.
     * @return the created renderer for chain calling
     */
    public Renderer add(String selector, List<?> list) {
        return add(create(selector, list));
    }

    /**
     * Create a renderer for list rendering by given parameter with given
     * {@link DataConvertor} and add it to the current renderer. See
     * {@link #create(String, List)}.
     * 
     * @param selector
     * @param list
     * @param convertor
     * @return the created renderer for chain calling
     */
    public <S, T> Renderer add(String selector, List<S> list, DataConvertor<S, T> convertor) {
        return add(create(selector, list, convertor));
    }

    /**
     * add a {@link DebugRenderer} to the current Renderer and when this
     * renderer is applied, the target element specified by the given selector
     * will be output by logger.
     * 
     * @param selector
     *            a css selector
     * @return the created renderer or the current renderer for chain calling
     */
    public Renderer addDebugger(String selector) {
        return DebugRenderer.logger.isDebugEnabled() ? add(create(selector, new DebugRenderer())) : this;
    }

    /**
     * add a {@link DebugRenderer} to the current Renderer and when this
     * renderer is applied, the current rendering element (see
     * {@link Context#setCurrentRenderingElement(Element)}) will be output by
     * logger.
     * 
     * @return the created renderer or the current renderer for chain calling
     */
    public Renderer addDebugger() {
        return DebugRenderer.logger.isDebugEnabled() ? add(new DebugRenderer()) : this;
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
    public final static Renderer create(String selector, long value) {
        return create(selector, String.valueOf(value));
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
    public final static Renderer create(String selector, int value) {
        return create(selector, String.valueOf(value));
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
    public final static Renderer create(String selector, boolean value) {
        return create(selector, String.valueOf(value));
    }

    /**
     * Create a renderer for text by given parameter.
     * <p>
     * All child nodes of the target element specified by selector will be
     * emptied and the given String value will be rendered as a single text node
     * of the target element.
     * 
     * @param selector
     *            a css selector
     * @param value
     *            a String value that will be rendered
     * @return the created renderer
     */
    public final static Renderer create(String selector, String value) {
        return create(selector, new TextSetter(value));
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
    public final static Renderer create(String selector, String attr, long value) {
        return create(selector, attr, String.valueOf(value));
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
    public final static Renderer create(String selector, String attr, int value) {
        return create(selector, attr, String.valueOf(value));
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
    public final static Renderer create(String selector, String attr, boolean value) {
        return create(selector, attr, String.valueOf(value));
    }

    /**
     * Create a renderer for attribute setting by given parameter.
     * <p>
     * An additional character of "+" or "-" can be used as a prefix of
     * attribute name. See detail at {@link AttriuteSetter}.
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
        return create(selector, new AttriuteSetter(attr, value));
    }

    /**
     * Create a renderer for attribute setting by given parameter.
     * <p>
     * An additional character of "+" or "-" can be used as a prefix of
     * attribute name. There is also a special logic for an instance with
     * arbitrary type. See detail at {@link AttriuteSetter}.
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
        return create(selector, new AttriuteSetter(attr, value));
    }

    /**
     * Create a renderer for element rendering by given parameter.
     * <p>
     * The target element specified by the selector will be completely replaced
     * by the given element.
     * 
     * @param selector
     *            a css selector
     * @param elem
     *            a element that to be rendered
     * @return the created renderer
     */
    public final static Renderer create(String selector, Element elem) {
        return new Renderer(selector, new ElementTransformer(elem));
    }

    /**
     * Create a renderer for element setting by given parameter.
     * <p>
     * The target element specified by the given selector will not be replaced
     * and will be passed to the given {@link ElementSetter} as a parameter.
     * 
     * @param selector
     *            a css selector
     * @param setter
     *            an ElementSetter
     * @return the created renderer
     */
    public final static Renderer create(String selector, ElementSetter setter) {
        return new Renderer(selector, new ElementSetterTransformer(setter));
    }

    /**
     * Create a renderer for recursive renderer rendering by given parameter.
     * <p>
     * The given renderer will be applied to element specified by the given
     * selector.
     * 
     * @param selector
     *            a css selector
     * @param renderer
     *            a renderer
     * @return the created renderer for chain calling
     */
    public final static Renderer create(String selector, Renderer renderer) {
        return new Renderer(selector, new RendererTransformer(renderer));
    }

    /**
     * Create a renderer for list rendering by given parameter.
     * <p>
     * The target Element specified by the given selector will be duplicated
     * times as the count of the given list and the contents of the list will be
     * applied to the target Element too.
     * 
     * @param selector
     *            a css selector
     * @param list
     *            a list that can contain all the types supported by the
     *            non-list add methods of Renderer
     * @return the created renderer
     */
    public final static Renderer create(String selector, List<?> list) {
        List<Transformer<?>> transformerList = new ArrayList<Transformer<?>>(list.size());
        for (Object obj : list) {
            transformerList.add(TransformerFactory.generateTransformer(obj));
        }
        return new Renderer(selector, transformerList);
    }

    /**
     * Create a renderer for list rendering by given parameter with given
     * {@link DataConvertor}. See {@link #create(String, List)}.
     * 
     * @param selector
     *            a css selector
     * @param list
     *            a list with arbitrary type data
     * @param convertor
     *            a convertor that can convert the arbitrary types of the list
     *            data to the types supported by the non-list create methods of
     *            Renderer
     * @return the created renderer
     */
    public final static <S, T> Renderer create(String selector, List<S> list, DataConvertor<S, T> convertor) {
        List<T> newList = new ArrayList<>();
        for (S obj : list) {
            newList.add(convertor.convert(obj));
        }
        return create(selector, newList);
    }

    public final static <S, T> Renderer create(String selector, List<S> list, final ParallelDataConvertor<S, T> convertor) {
        ExecutorService executor = Context.getCurrentThreadContext().getConfiguration().getMultiThreadExecutor();
        List<Future<T>> newList = new ArrayList<>();
        for (S obj : list) {
            newList.add(convertor.invoke(executor, obj));
        }
        return create(selector, newList);
    }
}
