package com.astamuse.asta4d.render.test;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;
import org.jsoup.nodes.Element;

import com.astamuse.asta4d.render.AttributeSetter;
import com.astamuse.asta4d.render.Renderer;
import com.astamuse.asta4d.render.RendererTestHelper;
import com.astamuse.asta4d.render.RendererType;
import com.astamuse.asta4d.render.transformer.Transformer;
import com.astamuse.asta4d.util.collection.ListConvertUtil;
import com.astamuse.asta4d.util.collection.RowConvertor;

public class RendererTester {
    private Renderer renderer;

    private RendererTester(Renderer renderer) {
        this.renderer = renderer;
    }

    public final static RendererTester forRenderer(Renderer renderer) {
        return new RendererTester(renderer);
    }

    public final static List<RendererTester> forRendererList(List<Renderer> rendererList) {

        return ListConvertUtil.transform(rendererList, new RowConvertor<Renderer, RendererTester>() {
            @Override
            public RendererTester convert(int rowIndex, Renderer obj) {
                return new RendererTester(obj);
            }
        });

    }

    private List<List<Transformer<?>>> retrieveNonAttrTransformer(String selector) {

        List<List<Transformer<?>>> rtnList = new LinkedList<>();

        List<Renderer> rendererList = renderer.asUnmodifiableList();
        for (Renderer r : rendererList) {
            if (r.getSelector().equals(selector)) {
                List<Transformer<?>> list = r.getTransformerList();
                if (list.isEmpty()) {
                    rtnList.add(list);
                } else {
                    Transformer<?> t = list.get(0);
                    if (t.getContent() instanceof AttributeSetter) {
                        continue;
                    } else {
                        rtnList.add(list);
                    }
                }
            }
        }
        return rtnList;
    }

    private List<Transformer<?>> retrieveSingleTransformerListOnSelector(String selector) {
        List<List<Transformer<?>>> list = retrieveNonAttrTransformer(selector);
        if (list.isEmpty()) {
            throw new RendererTestException("There is no value to be rendered for selector:[" + selector + "]");
        } else if (list.size() > 1) {
            throw new RendererTestException("There are more than one value to be rendered for selector:[" + selector +
                    "], maybe it is rendered multiple times?");
        } else {
            return list.get(0);
        }
    }

    // following are simple render value accessores for simple test cases
    private Transformer<?> retrieveSingleTransformerOnSelector(String selector) {
        List<Transformer<?>> list = retrieveSingleTransformerListOnSelector(selector);

        if (list.isEmpty()) {
            throw new RendererTestException("There is no value to be rendered for selector:[" + selector +
                    "], maybe it is rendered as a empty list?");
        } else if (list.size() > 1) {

            throw new RendererTestException("There are more than one value to be rendered for selector:[" + selector +
                    "], maybe it is rendered as a list?");
        } else {
            return list.get(0);
        }
    }

    public boolean noOp() {
        return renderer.asUnmodifiableList().size() == 1 && RendererTestHelper.getRendererType(renderer) == RendererType.GO_THROUGH;
    }

    public boolean noOp(String selector) {
        List<List<Transformer<?>>> list = retrieveNonAttrTransformer(selector);
        return list.isEmpty();
    }

    public boolean noOp(String selector, String attr) {
        List list = (List) getAttrAsObject(selector, attr, true, true);
        return list.isEmpty();
    }

    @SuppressWarnings("rawtypes")
    public Object get(String selector) {
        Transformer t = retrieveSingleTransformerOnSelector(selector);
        Object content = retrieveTestContentFromTransformer(t);
        return content;
    }

    public List<Object> getAsList(String selector) {
        return getAsList(selector, Object.class);
    }

    public <T> List<T> getAsList(String selector, final Class<T> targetCls) {
        List<Transformer<?>> list = retrieveSingleTransformerListOnSelector(selector);
        return ListConvertUtil.transform(list, new RowConvertor<Transformer<?>, T>() {
            @SuppressWarnings("unchecked")
            @Override
            public T convert(int rowIndex, Transformer<?> transformer) {
                Object content = retrieveTestContentFromTransformer(transformer);
                return (T) content;
            }
        });
    }

    private Object retrieveTestContentFromTransformer(Transformer<?> transformer) {
        Object content = null;
        if (transformer instanceof TestableRendering) {
            content = ((TestableRendering) transformer).retrieveTestableData();
        }

        if (content != null && content instanceof TestableRendering) {
            content = ((TestableRendering) content).retrieveTestableData();
        }

        if (content != null && content instanceof Element) {
            content = new TestableElementWrapper((Element) content);
        }
        return content;
    }

    public Object getAttr(String selector, String attr) {
        return getAttrAsObject(selector, attr, false, false);
    }

    /**
     * This method is only for retrieving rendered value of "+class" and "-class" attr action
     * 
     * @param selector
     * @param attr
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<String> getAttrAsList(String selector, String attr) {
        if (attr.equals("+class") || attr.equals("-class")) {
            return (List<String>) getAttrAsObject(selector, attr, true, false);
        } else {
            throw new RendererTestException("This method is only for retrieving rendered value of \"+class\" and \"-class\" attr action");
        }

    }

    private Object getAttrAsObject(String selector, String attr, boolean allowList, boolean allowEmpty) {
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

        if (!allowEmpty && valueList.isEmpty()) {
            throw new RendererTestException("There is no value to be rendered for attr:[" + attr + "] of selector:[" + selector + "]");
        } else if (valueList.size() > 1) {
            if (allowList) {
                return valueList;
            } else {
                throw new RendererTestException("There are more than one values(" + valueList.toString() + ") to be rendered for attr:[" +
                        attr + "] of selector:[" + selector + "]");
            }
        } else {
            if (allowList) {
                return valueList;
            } else {
                return valueList.get(0);
            }
        }

    }

}
