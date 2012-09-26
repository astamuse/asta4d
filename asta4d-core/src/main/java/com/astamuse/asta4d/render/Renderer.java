package com.astamuse.asta4d.render;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jsoup.nodes.Element;

import com.astamuse.asta4d.data.DataConvertor;
import com.astamuse.asta4d.transformer.ElementSetterTransformer;
import com.astamuse.asta4d.transformer.ElementTransformer;
import com.astamuse.asta4d.transformer.RendererTransformer;
import com.astamuse.asta4d.transformer.TextNodeTransformer;
import com.astamuse.asta4d.transformer.Transformer;

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

    // private in

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
     *            a int value that will be treated as a String
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
     * Create a renderer for text by given parameter and add it to the current
     * renderer.
     * <p>
     * All child nodes of the target element specified by selector will be
     * emptied and the given String value will be rendered as a single text node
     * of the target element.
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
     *            a int value that will be treated as a String value
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
     * the current renderer.
     * <p>
     * 
     * An additional character of "+" or "-" can be used as a prefix of
     * attribute name. See detail at {@link AttriuteSetter}.
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
     * the current renderer.
     * <p>
     * 
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
     * @return the created renderer for chain calling
     */
    public Renderer add(String selector, String attr, Object value) {
        return add(create(selector, attr, value));
    }

    /**
     * Create a renderer for element rendering by given parameter and add it to
     * the current renderer.
     * <p>
     * The target element specified by the selector will be completely replaced
     * by the given element.
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
     * the current renderer.
     * <p>
     * The target element that specified by the given selector will not be
     * replaced and will be passed to the given {@link ElementSetter} as a
     * parameter.
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
     * add it to the current renderer.
     * <p>
     * The given renderer will be applied to element that specified by the given
     * selector.
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

    public Renderer add(String selector, List<?> list) {
        return add(create(selector, list));
    }

    public <S, T> Renderer add(String selector, List<S> list, DataConvertor<S, T> convertor) {
        return add(create(selector, list, convertor));
    }

    public Renderer addDebugger(String selector) {
        return DebugRenderer.logger.isDebugEnabled() ? add(create(selector, new DebugRenderer())) : this;
    }

    public Renderer addDebugger() {
        return DebugRenderer.logger.isDebugEnabled() ? add(new DebugRenderer()) : this;
    }

    public final static Renderer create(String selector, long value) {
        return new Renderer(selector, new TextNodeTransformer(String.valueOf(value)));
    }

    public final static Renderer create(String selector, int value) {
        return new Renderer(selector, new TextNodeTransformer(String.valueOf(value)));
    }

    public final static Renderer create(String selector, boolean value) {
        return new Renderer(selector, new TextNodeTransformer(String.valueOf(value)));
    }

    public final static Renderer create(String selector, String value) {
        return new Renderer(selector, new TextNodeTransformer(value));
    }

    public final static Renderer create(String selector, String attr, long value) {
        return create(selector, attr, String.valueOf(value));
    }

    public final static Renderer create(String selector, String attr, int value) {
        return create(selector, attr, String.valueOf(value));
    }

    public final static Renderer create(String selector, String attr, boolean value) {
        return create(selector, attr, String.valueOf(value));
    }

    public final static Renderer create(String selector, String attr, Object value) {
        return create(selector, new AttriuteSetter(attr, value));
    }

    public final static Renderer create(String selector, String attr, String value) {
        return create(selector, new AttriuteSetter(attr, value));
    }

    public final static Renderer create(String selector, Element elem) {
        return new Renderer(selector, new ElementTransformer(elem));
    }

    public final static Renderer create(String selector, Renderer renderer) {
        return new Renderer(selector, new RendererTransformer(renderer));
    }

    public final static Renderer create(String selector, ElementSetter setter) {
        return new Renderer(selector, new ElementSetterTransformer(setter));
    }

    public final static <S, T> Renderer create(String selector, List<S> list, DataConvertor<S, T> convertor) {
        List<T> newList = new ArrayList<>();
        for (S obj : list) {
            newList.add(convertor.convert(obj));
        }
        return create(selector, newList);
    }

    public final static Renderer create(String selector, List<?> list) {
        List<Transformer<?>> transformerList = new ArrayList<Transformer<?>>(list.size());
        Transformer<?> transformer;
        for (Object obj : list) {
            if (obj instanceof String) {
                transformer = new TextNodeTransformer(obj.toString());
            } else if (obj instanceof Renderer) {
                transformer = new RendererTransformer((Renderer) obj);
            } else if (obj instanceof ElementSetter) {
                transformer = new ElementSetterTransformer((ElementSetter) obj);
            } else if (obj instanceof Element) {
                transformer = new ElementTransformer((Element) obj);
            } else {
                transformer = new TextNodeTransformer(obj.toString());
            }
            transformerList.add(transformer);
        }
        return new Renderer(selector, transformerList);
    }
}
