package com.astamuse.asta4d.render.test;

import java.util.Collections;
import java.util.List;

import com.astamuse.asta4d.render.AttributeSetter;
import com.astamuse.asta4d.render.Renderer;
import com.astamuse.asta4d.render.TextSetter;
import com.astamuse.asta4d.render.transformer.Transformer;

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

    private String retrieveSingleStringOnSelector(String selector) {
        Transformer t = retrieveSingleTransformerOnSelector(selector);
        Object setter = t.getContent();
        if (setter instanceof TextSetter) {
            return ((TextSetter) setter).getText();
        } else {
            String msg = "Expected text setter on selector [%s] but [%] found";
            msg = String.format(msg, selector, setter.toString());
            throw new RendererTestException(msg);
        }
    }

    public String getRenderValueAsString(String selector) {
        return retrieveSingleStringOnSelector(selector);
    }

    public Long getRenderValueAsLong(String selector) {
        String s = getRenderValueAsString(selector);
        if (s == null) {
            return null;
        } else {
            return Long.valueOf(s);
        }
    }

    public Integer getRenderValueAsInteger(String selector) {
        String s = getRenderValueAsString(selector);
        if (s == null) {
            return null;
        } else {
            return Integer.valueOf(s);
        }
    }

    public Boolean getRenderValueAsBoolean(String selector) {
        String s = getRenderValueAsString(selector);
        if (s == null) {
            return null;
        } else {
            return Boolean.valueOf(s);
        }
    }

    public String getRenderAttrValue(String selector, String attr) {
        List<Renderer> rendererList = renderer.asUnmodifiableList();
        for (Renderer r : rendererList) {
            if (r.getSelector().equals(selector)) {
                List<Transformer<?>> list = r.getTransformerList();
                if (list.isEmpty()) {
                    continue;
                } else {
                    Transformer<?> t = list.get(0);
                    if (t.getContent() instanceof AttributeSetter) {
                        AttributeSetter setter = (AttributeSetter) t.getContent();
                        if (setter.getOriginalAttrName().equals(attr)) {
                            Object obj = setter.getOriginalValue();
                            if (obj == null) {
                                return null;
                            } else {
                                return obj.toString();
                            }
                        }
                    } else {
                        continue;
                    }
                }
            }
        }

        throw new RendererTestException("There is no value to be rendered for attr:[" + attr + "] of selector:[" + selector + "]");
    }

}
