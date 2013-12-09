package com.astamuse.asta4d.render.test;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

import com.astamuse.asta4d.render.AttributeSetter;
import com.astamuse.asta4d.render.Renderer;
import com.astamuse.asta4d.render.transformer.Transformer;
import com.astamuse.asta4d.util.collection.ListConvertUtil;
import com.astamuse.asta4d.util.collection.RowConvertor;

public class RendererTester {
    private Renderer renderer;

    public RendererTester(Renderer renderer) {
        this.renderer = renderer;
    }

    private List<Transformer<?>> retrieveNonAttrTransformer(String selector) {

        List<Renderer> rendererList = renderer.asUnmodifiableList();
        for (Renderer r : rendererList) {
            if (r.getSelector().equals(selector)) {
                List<Transformer<?>> list = r.getTransformerList();
                if (list.isEmpty()) {
                    return list;
                } else {
                    Transformer<?> t = list.get(0);
                    if (t.getContent() instanceof AttributeSetter) {
                        continue;
                    } else {
                        return list;
                    }
                }
            }
        }
        return Collections.emptyList();
    }

    // following are simple render value accessores for simple test cases
    private Transformer<?> retrieveSingleTransformerOnSelector(String selector) {
        List<Transformer<?>> list = retrieveNonAttrTransformer(selector);
        if (list.isEmpty()) {
            throw new RendererTestException("There is no value to be rendered for selector:[" + selector + "]");
        } else if (list.size() > 1) {
            throw new RendererTestException("There are more than one value to be rendered for selector:[" + selector +
                    "], may be it is rendered as a list?");
        } else {
            return list.get(0);
        }
    }

    @SuppressWarnings("rawtypes")
    public Object get(String selector) {
        Transformer t = retrieveSingleTransformerOnSelector(selector);
        Object content = t.getContent();
        if (content != null && content instanceof TestableElementSetter) {
            content = ((TestableElementSetter) content).retrieveTestableData();
        }
        return content;
    }

    public List<Object> getAsList(String selector) {
        return getAsList(selector, Object.class);
    }

    public <T> List<T> getAsList(String selector, final Class<T> targetCls) {
        List<Transformer<?>> list = retrieveNonAttrTransformer(selector);
        return ListConvertUtil.transform(list, new RowConvertor<Transformer<?>, T>() {
            @SuppressWarnings("unchecked")
            @Override
            public T convert(int rowIndex, Transformer<?> transformer) {
                Object content = transformer.getContent();
                if (content != null && content instanceof TestableElementSetter) {
                    content = ((TestableElementSetter) content).retrieveTestableData();
                }
                return (T) content;
            }
        });
    }

    public String getAttr(String selector, String attr) {
        return (String) getAttrAsObject(selector, attr);
    }

    public Object getAttrAsObject(String selector, String attr) {
        List<Renderer> rendererList = renderer.asUnmodifiableList();
        List<Object> valueList = new LinkedList<>();
        for (Renderer r : rendererList) {
            if (r.getSelector().equals(selector)) {
                List<Transformer<?>> list = r.getTransformerList();
                if (list.isEmpty()) {
                    continue;
                } else {
                    Transformer<?> t = list.get(0);
                    if (t.getContent() instanceof AttributeSetter) {
                        AttributeSetter setter = (AttributeSetter) t.getContent();
                        @SuppressWarnings("unchecked")
                        Pair<String, Object> data = (Pair<String, Object>) setter.retrieveTestableData();
                        if (data.getKey().equals(attr)) {
                            Object obj = data.getValue();
                            valueList.add(obj);
                        }
                    } else {
                        continue;
                    }
                }
            }
        }

        if (valueList.isEmpty()) {
            throw new RendererTestException("There is no value to be rendered for attr:[" + attr + "] of selector:[" + selector + "]");
        } else if (valueList.size() > 1) {
            throw new RendererTestException("There are more than one values(" + valueList.toString() + ") to be rendered for attr:[" +
                    attr + "] of selector:[" + selector + "]");
        } else {
            return valueList.get(0);
        }

    }

}
