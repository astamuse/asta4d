package org.jsoupit.template.render;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.nodes.Element;
import org.jsoupit.template.transformer.ElementSetterTransformer;
import org.jsoupit.template.transformer.ElementTransformer;
import org.jsoupit.template.transformer.RendererTransformer;
import org.jsoupit.template.transformer.TextNodeTransformer;
import org.jsoupit.template.transformer.Transformer;

public class Renderer {

    private String selector;

    private List<Transformer<?>> transformerList;

    private Renderer next;

    private Renderer head;

    /*
     * public Renderer(){ selector = ""; transformerList = new
     * ArrayList<Transformer<?>>(); next = null; }
     */

    public Renderer(String selector, Transformer<?> transformer) {
        this.selector = selector;
        this.transformerList = new ArrayList<Transformer<?>>(1);
        transformerList.add(transformer);
        next = null;
        head = this;
    }

    public Renderer(String selector, List<Transformer<?>> transformerList) {
        this.selector = selector;
        this.transformerList = transformerList;
        next = null;
        head = this;
    }

    public String getSelector() {
        return selector;
    }

    public List<Transformer<?>> getTransformerList() {
        return transformerList;
    }

    @Override
    public String toString() {
        return "selector:" + selector;
    }

    public Renderer getNext() {
        return next;
    }

    public Renderer add(Renderer renderer) {
        Renderer addPoint = this;
        while (addPoint.next != null) {
            addPoint = addPoint.next;
        }
        addPoint.next = renderer;
        renderer.head = addPoint.head;
        // should return the top in the chain
        // TODO we should rewrite the add logic and RenderUtil should always try
        // to get the head
        return head;
    }

    public Renderer add(String selector, long value) {
        return add(create(selector, value));
    }

    public Renderer add(String selector, int value) {
        return add(create(selector, value));
    }

    public Renderer add(String selector, String value) {
        return add(create(selector, value));
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

    public <S, T> Renderer add(String selector, List<S> list, ListConvertor<S, T> convertor) {
        return add(create(selector, list, convertor));
    }

    public Renderer add(String selector, List<?> list) {
        return add(create(selector, list));
    }

    public final static Renderer create(String selector, long value) {
        return new Renderer(selector, new TextNodeTransformer(String.valueOf(value)));
    }

    public final static Renderer create(String selector, int value) {
        return new Renderer(selector, new TextNodeTransformer(String.valueOf(value)));
    }

    public final static Renderer create(String selector, String value) {
        return new Renderer(selector, new TextNodeTransformer(value));
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

    public final static <S, T> Renderer create(String selector, List<S> list, ListConvertor<S, T> convertor) {
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
            if (transformer != null) {
                transformerList.add(transformer);
            }
        }
        return new Renderer(selector, transformerList);
    }
}
