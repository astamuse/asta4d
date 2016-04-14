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
package com.astamuse.asta4d.web.form.flow.base;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Tag;

import com.astamuse.asta4d.data.ContextDataHolder;
import com.astamuse.asta4d.data.InjectTrace;
import com.astamuse.asta4d.render.ElementSetter;
import com.astamuse.asta4d.render.Renderer;
import com.astamuse.asta4d.util.ElementUtil;
import com.astamuse.asta4d.util.SelectorUtil;
import com.astamuse.asta4d.util.annotation.AnnotatedPropertyInfo;
import com.astamuse.asta4d.web.form.CascadeArrayFunctions;
import com.astamuse.asta4d.web.form.annotation.CascadeFormField;
import com.astamuse.asta4d.web.form.field.FormFieldPrepareRenderer;

public interface BasicFormFlowSnippetTrait extends CascadeArrayFunctions {

    /**
     * Sub class should tell us the current rendering mode. Since we have no any information about the concrete cases, we always return true
     * by default.
     * 
     * @param step
     * @param form
     * @param fieldName
     * @return
     */
    default boolean renderForEdit(String step, Object form, String fieldName) {
        return true;
    }

    /**
     * The entry of form rendering. Sub classes could override it in case of necessarily.
     * 
     * @return
     * @throws Exception
     */
    default Renderer render(FormRenderingData renderingData) throws Exception {
        Renderer renderer = preRender(renderingData);
        renderer.add(renderTraceId(renderingData.getTraceId()));
        Object form = retrieveRenderTargetForm(renderingData);
        renderer.add(renderForm(renderingData.getRenderTargetStep(), form, EMPTY_INDEXES));
        Element clientJs = retrieveClientCascadeUtilJsContent();
        if (clientJs != null) {
            renderer.add(":root", (Element elem) -> {
                elem.appendChild(clientJs);
            });
        }
        renderer.add(postRender(renderingData));
        return renderer;
    }

    default Renderer preRender(FormRenderingData renderingData) {
        return Renderer.create();
    }

    default Renderer postRender(FormRenderingData renderingData) {
        return Renderer.create();
    }

    /**
     * We only render the form trace map when it exists
     * 
     * @return
     */
    default Renderer renderTraceId(String traceId) {
        if (StringUtils.isEmpty(traceId)) {
            return Renderer.create();
        } else {
            return Renderer.create(":root", new ElementSetter() {
                @Override
                public void set(Element elem) {
                    Element hide = new Element(Tag.valueOf("input"), "");
                    hide.attr("name", FormFlowConstants.FORM_FLOW_TRACE_ID_QUERY_PARAM);
                    hide.attr("type", "hidden");
                    hide.attr("value", traceId);
                    elem.appendChild(hide);
                }
            });
        }
    }

    default Object retrieveRenderTargetForm(FormRenderingData renderingData) {
        return renderingData.getTraceData().getStepFormMap().get(renderingData.getRenderTargetStep());
    }

    /**
     * 
     * PriorRenderMethod the whole given form instance. All the {@link FormFieldPrepareRenderer}s would be invoked here too.
     * 
     * @param renderTargetStep
     * @param form
     * @param indexes
     * @return
     * @throws Exception
     */
    default Renderer renderForm(String renderTargetStep, Object form, int[] indexes) throws Exception {
        Renderer render = Renderer.create();
        if (form == null) {
            return render;
        }

        if (form instanceof StepRepresentableForm) {
            String[] formRepresentingSteps = ((StepRepresentableForm) form).retrieveRepresentingSteps();
            if (ArrayUtils.contains(formRepresentingSteps, renderTargetStep)) {
                // it is OK
            } else {
                return render;
            }
        }

        render.disableMissingSelectorWarning();

        render.add(preRenderForm(renderTargetStep, form, indexes));

        List<FormFieldPrepareRenderer> fieldDataPrepareRendererList = retrieveFieldPrepareRenderers(renderTargetStep, form);

        for (FormFieldPrepareRenderer formFieldDataPrepareRenderer : fieldDataPrepareRendererList) {
            BasicFormFlowTraitHelper.FieldRenderingInfo renderingInfo = BasicFormFlowTraitHelper.getRenderingInfo(this,
                    formFieldDataPrepareRenderer.targetField(), indexes);
            render.add(formFieldDataPrepareRenderer.preRender(renderingInfo.editSelector, renderingInfo.displaySelector));
        }

        render.add(renderValueOfFields(renderTargetStep, form, indexes));

        for (FormFieldPrepareRenderer formFieldDataPrepareRenderer : fieldDataPrepareRendererList) {
            BasicFormFlowTraitHelper.FieldRenderingInfo renderingInfo = BasicFormFlowTraitHelper.getRenderingInfo(this,
                    formFieldDataPrepareRenderer.targetField(), indexes);
            render.add(formFieldDataPrepareRenderer.postRender(renderingInfo.editSelector, renderingInfo.displaySelector));
        }

        render.add(postRenderForm(renderTargetStep, form, indexes));

        return render.enableMissingSelectorWarning();
    }

    default Renderer preRenderForm(String renderTargetStep, Object form, int[] indexes) throws Exception {
        return Renderer.create();
    }

    default Renderer postRenderForm(String renderTargetStep, Object form, int[] indexes) throws Exception {
        return Renderer.create();
    }

    /**
     * 
     * PriorRenderMethod the value of all the given form's fields.The rendering of cascade forms will be done here as well(recursively call
     * the {@link #renderForm(String, Object, int)}).
     * 
     * @param renderTargetStep
     * @param form
     * @param indexes
     * @return
     * @throws Exception
     */
    default Renderer renderValueOfFields(String renderTargetStep, Object form, int[] indexes) throws Exception {
        Renderer render = Renderer.create();
        List<AnnotatedPropertyInfo> fieldList = BasicFormFlowTraitHelper.retrieveRenderTargetFieldList(form);

        for (AnnotatedPropertyInfo field : fieldList) {

            Object v = field.retrieveValue(form);

            CascadeFormField cff = field.getAnnotation(CascadeFormField.class);
            if (cff != null) {
                String containerSelector = cff.containerSelector();

                if (field.getType().isArray()) {// a cascade form for array
                    int len = Array.getLength(v);
                    List<Renderer> subRendererList = new ArrayList<>(len);
                    int loopStart = 0;
                    if (renderForEdit(renderTargetStep, form, cff.name())) {
                        // for rendering a template DOM
                        loopStart = -1;
                    }
                    Class<?> subFormType = field.getType().getComponentType();
                    Object subForm;
                    for (int i = loopStart; i < len; i++) {
                        int[] newIndex = indexes.clone();

                        // retrieve the form instance
                        if (i >= 0) {
                            newIndex = ArrayUtils.add(newIndex, i);
                            subForm = Array.get(v, i);
                        } else {
                            // create a template instance
                            subForm = createFormInstanceForCascadeFormArrayTemplate(subFormType);
                        }

                        Renderer subRenderer = Renderer.create();

                        // only rewrite the refs for normal instances
                        if (i >= 0) {
                            subRenderer.add(rewriteCascadeFormFieldArrayRef(renderTargetStep, subForm, newIndex));
                        }

                        subRenderer.add(renderForm(renderTargetStep, subForm, newIndex));

                        // hide the template DOM
                        if (i < 0) {
                            subRenderer.add(":root", hideCascadeFormTemplateDOM(subFormType));
                        }

                        subRendererList.add(subRenderer);
                    }
                    containerSelector = rewriteArrayIndexPlaceHolder(containerSelector, indexes);
                    render.add(containerSelector, subRendererList);
                } else {// a simple cascade form

                    if (StringUtils.isNotEmpty(containerSelector)) {
                        render.add(containerSelector, renderForm(renderTargetStep, v, indexes));
                    } else {
                        render.add(renderForm(renderTargetStep, v, indexes));
                    }
                }
                continue;
            }

            if (v == null) {
                @SuppressWarnings("rawtypes")
                ContextDataHolder valueHolder;

                if (field.getField() != null) {
                    valueHolder = InjectTrace.getInstanceInjectionTraceInfo(form, field.getField());
                } else {
                    valueHolder = InjectTrace.getInstanceInjectionTraceInfo(form, field.getSetter());
                }

                if (valueHolder != null) {
                    v = convertRawInjectionTraceDataToRenderingData(field.getName(), field.getType(), valueHolder.getFoundOriginalData());
                }
            }

            BasicFormFlowTraitHelper.FieldRenderingInfo renderingInfo = BasicFormFlowTraitHelper.getRenderingInfo(this, field, indexes);

            // render.addDebugger("whole form before: " + field.getName());

            if (renderForEdit(renderTargetStep, form, field.getName())) {
                render.add(renderingInfo.valueRenderer.renderForEdit(renderingInfo.editSelector, v));
            } else {
                render.add(renderingInfo.valueRenderer.renderForDisplay(renderingInfo.editSelector, renderingInfo.displaySelector, v));
            }
        }
        return render;
    }

    default String defaultDisplayElementSelectorForField(String fieldName) {
        return SelectorUtil.id(fieldName + "-display");
    }

    default String defaultEditElementSelectorForField(String fieldName) {
        return SelectorUtil.attr("name", fieldName);
    }

    default Renderer hideCascadeFormTemplateDOM(Class<?> subFormType) {
        return Renderer.create(":root", new ElementSetter() {
            @Override
            public void set(Element elem) {
                String style = elem.attr("style");
                if (StringUtils.isEmpty(style)) {
                    style = "display:none";
                } else {
                    if (!style.endsWith(";")) {
                        style += ";";
                    }
                    style += "display:none";
                }
                elem.attr("style", style);
            }
        });
    }

    @SuppressWarnings("rawtypes")
    default Object createFormInstanceForCascadeFormArrayTemplate(Class subFormType) throws InstantiationException, IllegalAccessException {
        return subFormType.newInstance();
    }

    /**
     * Sub classes could override this method to customize how to rewrite the array index for cascade array forms.
     * 
     * @param renderTargetStep
     * @param form
     * @param indexes
     * @return
     */
    default Renderer rewriteCascadeFormFieldArrayRef(final String renderTargetStep, final Object form, final int[] indexes) {

        final String[] targetAttrs = rewriteCascadeFormFieldArrayRefTargetAttrs();
        String[] attrSelectors = new String[targetAttrs.length];
        for (int i = 0; i < attrSelectors.length; i++) {
            attrSelectors[i] = SelectorUtil.attr(targetAttrs[i]);
        }

        return Renderer.create(StringUtils.join(attrSelectors, ","), new ElementSetter() {
            @Override
            public void set(Element elem) {
                String v;
                for (String attr : targetAttrs) {
                    v = elem.attr(attr);
                    if (StringUtils.isNotEmpty(v)) {
                        elem.attr(attr, rewriteArrayIndexPlaceHolder(v, indexes));
                    }
                }
            }
        });
    }

    /**
     * The attributes returned by this method will be rewritten for array index.
     * <p>
     * The default is {"id", "name", "cascade-ref", "cascade-ref-target", "cascade-ref-info-1", ..., "cascade-ref-info-9"}.
     * 
     * @return
     */
    default String[] rewriteCascadeFormFieldArrayRefTargetAttrs() {
        return BasicFormFlowTraitHelper.DefaultCascadeFormFieldArrayRefTargetAttrs;
    }

    /**
     * Sub classes should override this method to supply field prepare renderers.
     * 
     * @return
     * @throws Exception
     */
    default List<FormFieldPrepareRenderer> retrieveFieldPrepareRenderers(String renderTargetStep, Object form) {
        return new LinkedList<>();
    }

    /**
     * Sub classes could override this method to customize how to handle the injection trace data for type unmatch errors.
     * 
     * @param fieldName
     * @param fieldDataType
     * @param rawTraceData
     * @return
     */
    default Object convertRawInjectionTraceDataToRenderingData(String fieldName, Class<?> fieldDataType, Object rawTraceData) {
        if (fieldDataType.isArray() && rawTraceData.getClass().isArray()) {
            return rawTraceData;
        } else if (rawTraceData.getClass().isArray()) {// but field data type is
                                                       // not array
            if (Array.getLength(rawTraceData) > 0) {
                return Array.get(rawTraceData, 0);
            } else {
                return null;
            }
        } else {
            return rawTraceData;
        }
    }

    default Element retrieveClientCascadeUtilJsContent() {
        String exportName = clientCascadeUtilJsExportName();
        if (exportName == null) {
            return null;
        }

        if (BasicFormFlowTraitHelper.ClientCascadeJsContentCache != null) {
            return BasicFormFlowTraitHelper.ClientCascadeJsContentCache.clone();
        }

        StringBuilder jsContent = new StringBuilder(300);
        jsContent.append("<script>\n");
        jsContent.append("var ").append(exportName).append("=(\n");

        try (InputStream jsInput = clientCascadeUtilJsInputStream()) {
            jsContent.append(IOUtils.toString(jsInput, StandardCharsets.UTF_8));
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }

        jsContent.append(")();\n");
        jsContent.append("</script>");

        BasicFormFlowTraitHelper.ClientCascadeJsContentCache = ElementUtil.parseAsSingle(jsContent.toString());

        return BasicFormFlowTraitHelper.ClientCascadeJsContentCache.clone();
    }

    default String clientCascadeUtilJsExportName() {
        return null;
    }

    default InputStream clientCascadeUtilJsInputStream() {
        String jsPath = "/com/astamuse/asta4d/web/form/js/ClientCascadeUtil.js";
        return this.getClass().getClassLoader().getResourceAsStream(jsPath);
    }
}
