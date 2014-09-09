package com.astamuse.asta4d.web.form.field.impl;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.jsoup.nodes.Element;
import org.jsoup.parser.Tag;

import com.astamuse.asta4d.extnode.GroupNode;
import com.astamuse.asta4d.render.ElementNotFoundHandler;
import com.astamuse.asta4d.render.ElementSetter;
import com.astamuse.asta4d.render.Renderable;
import com.astamuse.asta4d.render.Renderer;
import com.astamuse.asta4d.render.transformer.ElementTransformer;
import com.astamuse.asta4d.util.ElementUtil;
import com.astamuse.asta4d.util.SelectorUtil;
import com.astamuse.asta4d.util.collection.ListConvertUtil;
import com.astamuse.asta4d.util.collection.RowConvertor;

public class CheckBoxRenderer extends RadioBoxRenderer {

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private List<String> convertValueToList(Object value) {
        if (value == null) {
            return new LinkedList<>();
        } else if (value.getClass().isArray()) {
            List<Object> list = Arrays.asList((Object[]) value);
            return ListConvertUtil.transform(list, new RowConvertor<Object, String>() {
                @Override
                public String convert(int rowIndex, Object obj) {
                    return getNonNullString(obj);
                }
            });
        } else if (value instanceof Iterable) {
            return ListConvertUtil.transform((Iterable) value, new RowConvertor<Object, String>() {
                @Override
                public String convert(int rowIndex, Object obj) {
                    return getNonNullString(obj);
                }
            });
        } else {
            return Arrays.asList(getNonNullString(value));
        }
    }

    @Override
    public Renderer renderForEdit(String editTargetSelector, Object value) {
        List<String> list = convertValueToList(value);
        Renderer renderer = Renderer.create();
        for (String nonNullString : list) {
            if (!nonNullString.isEmpty()) {
                renderer.add(SelectorUtil.attr("value", nonNullString), "checked", "");
            }
        }
        return renderer;
    }

    @Override
    public Renderer renderForDisplay(final String editTargetSelector, final String displayTargetSelector, Object value) {
        Renderer render = Renderer.create();

        // retrieve and create a value map here
        render.add(retrieveAndCreateValueMap(editTargetSelector, displayTargetSelector));

        // hide the edit element
        render.add(editTargetSelector, new Renderable() {
            @Override
            public Renderer render() {
                return hideTarget(editTargetSelector);
            }
        });

        // render.addDebugger("before " + displayTargetSelector);

        final List<String> valueList = convertValueToList(value);

        // render the shown value to target element by displayTargetSelector
        render.add(displayTargetSelector, new Renderable() {

            @Override
            public Renderer render() {
                List<String> displayString = ListConvertUtil.transform(valueList, new RowConvertor<String, String>() {
                    @Override
                    public String convert(int rowIndex, String v) {
                        return retrieveDisplayString(editTargetSelector, v);
                    }

                });
                return Renderer.create(displayTargetSelector, displayString);
            }
        });

        // if the element by displayTargetSelector does not exists, simply add a span to show the value.
        // since ElementNotFoundHandler has been delayed, so the Renderable is not necessary
        render.add(new ElementNotFoundHandler(displayTargetSelector) {
            @Override
            public Renderer alternativeRenderer() {
                return addAlternativeDom(editTargetSelector, valueList);
            }
        });
        return render;
    }

    protected Renderer addAlternativeDom(final String editTargetSelector, final List<String> valueList) {
        Renderer renderer = Renderer.create();

        final List<String> matchedIdList = new LinkedList<>();
        final List<String> unMatchedIdList = new LinkedList<>();

        renderer.add(editTargetSelector, new Renderable() {
            @Override
            public Renderer render() {
                Renderer renderer = Renderer.create("input", new ElementSetter() {
                    @Override
                    public void set(Element elem) {
                        if (valueList.contains(elem.attr("value"))) {
                            matchedIdList.add(elem.id());
                        } else {
                            unMatchedIdList.add(elem.id());
                        }
                    }
                });
                return renderer;
            }
        });

        renderer.add(":root", new Renderable() {
            @Override
            public Renderer render() {
                Renderer renderer = Renderer.create();
                for (String inputId : unMatchedIdList) {
                    renderer.add(hideTarget(SelectorUtil.attr(RadioBoxAdditionalRenderer.LABEL_REF_ATTR, inputId)));
                }
                if (matchedIdList.isEmpty()) {
                    renderer.add(addDefaultAlternativeDom(editTargetSelector, valueList));
                } else {
                    // do nothing for remaining the existing label element
                }
                return renderer;
            }
        });

        return renderer;

    }

    protected Renderer addDefaultAlternativeDom(final String editTargetSelector, final List<String> valueList) {
        Renderer renderer = Renderer.create();
        renderer.addDebugger("before alternative display for " + editTargetSelector);
        renderer.add(new Renderer(editTargetSelector, new ElementTransformer(null) {
            @Override
            public Element invoke(Element elem) {
                GroupNode group = new GroupNode();

                Element editClone = ElementUtil.safeClone(elem);
                group.appendChild(editClone);

                for (String v : valueList) {
                    String nonNullString = retrieveDisplayString(editTargetSelector, v);
                    Element newElem = new Element(Tag.valueOf("span"), "");
                    newElem.text(nonNullString);
                    group.appendChild(newElem);
                }
                return group;
            }

        }));
        renderer.addDebugger("after alternative display for " + editTargetSelector);
        return renderer;
    }
}
