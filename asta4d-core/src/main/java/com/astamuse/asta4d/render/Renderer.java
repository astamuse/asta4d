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

public class Renderer {

    // private in

    private String selector;

    private List<Transformer<?>> transformerList;

    private List<Renderer> chain;

    /*
     * public Renderer(){ selector = ""; transformerList = new
     * ArrayList<Transformer<?>>(); next = null; }
     */

    public Renderer(String selector, Transformer<?> transformer) {
        List<Transformer<?>> list = new ArrayList<>();
        list.add(transformer);
        init(selector, list);
    }

    public Renderer(String selector, List<Transformer<?>> transformerList) {
        init(selector, transformerList);
    }

    public void init(String selector, List<Transformer<?>> transformerList) {
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

    public List<Renderer> asUnmodifiableList() {
        return Collections.unmodifiableList(new ArrayList<>(this.chain));
    }

    public Renderer add(Renderer renderer) {
        this.chain.addAll(renderer.chain);
        for (Renderer r : renderer.chain) {
            r.chain = this.chain;
        }
        return renderer;
    }

    public Renderer add(String selector, long value) {
        return add(create(selector, value));
    }

    public Renderer add(String selector, int value) {
        return add(create(selector, value));
    }

    public Renderer add(String selector, boolean value) {
        return add(create(selector, value));
    }

    public Renderer add(String selector, String value) {
        return add(create(selector, value));
    }

    public Renderer add(String selector, String attr, long value) {
        return add(create(selector, attr, value));
    }

    public Renderer add(String selector, String attr, int value) {
        return add(create(selector, attr, value));
    }

    public Renderer add(String selector, String attr, boolean value) {
        return add(create(selector, attr, value));
    }

    public Renderer add(String selector, String attr, Object value) {
        return add(create(selector, attr, value));
    }

    public Renderer add(String selector, String attr, String value) {
        return add(create(selector, attr, value));
    }

    public Renderer add(String selector, Element elem) {
        return add(create(selector, elem));
    }

    public Renderer add(String selector, Renderer renderer) {
        return add(create(selector, renderer));
    }

    public Renderer add(String selector, ElementSetter setter) {
        return add(create(selector, setter));
    }

    public <S, T> Renderer add(String selector, List<S> list, DataConvertor<S, T> convertor) {
        return add(create(selector, list, convertor));
    }

    public Renderer add(String selector, List<?> list) {
        return add(create(selector, list));
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
        // TODO convert object to a data reference
        return create(selector, attr, String.valueOf(value));
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
