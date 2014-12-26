/*
 * Copyright 2014 astamuse company,Ltd.
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
package com.astamuse.asta4d.web.form.field.impl;

import static com.astamuse.asta4d.render.SpecialRenderer.Clear;

import java.util.LinkedList;
import java.util.List;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.astamuse.asta4d.extnode.GroupNode;
import com.astamuse.asta4d.render.ElementNotFoundHandler;
import com.astamuse.asta4d.render.ElementSetter;
import com.astamuse.asta4d.render.Renderable;
import com.astamuse.asta4d.render.Renderer;
import com.astamuse.asta4d.render.transformer.ElementTransformer;
import com.astamuse.asta4d.util.collection.RowRenderer;
import com.astamuse.asta4d.web.form.field.OptionValueMap;
import com.astamuse.asta4d.web.form.field.OptionValuePair;
import com.astamuse.asta4d.web.form.field.PrepareRenderingDataUtil;
import com.astamuse.asta4d.web.form.field.SimpleFormFieldWithOptionValueRenderer;

public abstract class AbstractSelectRenderer extends SimpleFormFieldWithOptionValueRenderer {

    @Override
    public Renderer renderForEdit(String editTargetSelector, Object value) {
        final List<String> valueList = convertValueToList(value);
        Renderer renderer = Renderer.create("option", "selected", Clear);
        String selector = "option";
        // we have to iterate the elements because the attr selector would not work for blank values.
        renderer.add(selector, new ElementSetter() {
            @Override
            public void set(Element elem) {
                String val = elem.attr("value");
                if (valueList.contains(val)) {
                    elem.attr("selected", "");
                }
            }
        });
        return Renderer.create(editTargetSelector, renderer);
    }

    @Override
    public Renderer renderForDisplay(final String editTargetSelector, final String displayTargetSelector, final Object value) {

        Renderer render = Renderer.create().disableMissingSelectorWarning();

        // retrieve and create a value map here
        if (PrepareRenderingDataUtil.retrieveStoredDataFromContextBySelector(editTargetSelector) == null) {

            render.add(editTargetSelector, new ElementSetter() {
                @Override
                public void set(Element elem) {
                    final List<OptionValuePair> optionList = new LinkedList<>();
                    Elements opts = elem.select("option");
                    String value, displayText;
                    for (Element opt : opts) {
                        value = opt.attr("value");
                        displayText = opt.text();
                        optionList.add(new OptionValuePair(value, displayText));
                    }
                    PrepareRenderingDataUtil.storeDataToContextBySelector(editTargetSelector, displayTargetSelector, new OptionValueMap(
                            optionList));
                }
            });
        }

        // hide target
        render.add(hideTarget(editTargetSelector));

        final List<String> valueList = convertValueToList(value);

        // render the shown value to target element by displayTargetSelector
        render.add(displayTargetSelector, new Renderable() {

            @Override
            public Renderer render() {
                return Renderer.create(displayTargetSelector, valueList, new RowRenderer<String>() {
                    @Override
                    public Renderer convert(int rowIndex, String v) {
                        return renderToDisplayTarget(displayTargetSelector,
                                retrieveDisplayStringFromStoredOptionValueMap(displayTargetSelector, v));
                    }
                });
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

        return render.enableMissingSelectorWarning();
    }

    protected Renderer addAlternativeDom(final String editTargetSelector, final List<String> valueList) {
        return new Renderer(editTargetSelector, new ElementTransformer(null) {
            @Override
            public Element invoke(Element elem) {
                GroupNode group = new GroupNode();

                Element editClone = elem.clone();
                group.appendChild(editClone);

                for (String v : valueList) {
                    String nonNullString = retrieveDisplayStringFromStoredOptionValueMap(editTargetSelector, v);
                    group.appendChild(createAlternativeDisplayElement(nonNullString));
                }
                return group;
            }// invoke
        });
    }

}
