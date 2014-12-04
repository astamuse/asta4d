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

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Tag;

import com.astamuse.asta4d.Configuration;
import com.astamuse.asta4d.Context;
import com.astamuse.asta4d.data.ContextDataHolder;
import com.astamuse.asta4d.data.InjectTrace;
import com.astamuse.asta4d.data.annotation.ContextData;
import com.astamuse.asta4d.render.ElementSetter;
import com.astamuse.asta4d.render.Renderer;
import com.astamuse.asta4d.util.SelectorUtil;
import com.astamuse.asta4d.util.annotation.AnnotatedPropertyInfo;
import com.astamuse.asta4d.util.annotation.AnnotatedPropertyUtil;
import com.astamuse.asta4d.web.form.CascadeFormUtil;
import com.astamuse.asta4d.web.form.annotation.CascadeFormField;
import com.astamuse.asta4d.web.form.annotation.FormField;
import com.astamuse.asta4d.web.form.field.FormFieldPrepareRenderer;
import com.astamuse.asta4d.web.form.field.FormFieldValueRenderer;

public abstract class AbstractFormFlowSnippet {

    private static class FieldRenderingInfo {
        String editSelector;
        String displaySelector;
        FormFieldValueRenderer valueRenderer;

        FieldRenderingInfo replaceArrayIndex(int index) {
            FieldRenderingInfo newInfo = new FieldRenderingInfo();
            newInfo.editSelector = CascadeFormUtil.rewriteArrayIndexPlaceHolder(editSelector, index);
            newInfo.displaySelector = CascadeFormUtil.rewriteArrayIndexPlaceHolder(displaySelector, index);
            newInfo.valueRenderer = valueRenderer;
            return newInfo;
        }
    }

    private static final Map<AnnotatedPropertyInfo, FieldRenderingInfo> FieldRenderingInfoMap = new ConcurrentHashMap<>();

    @ContextData(name = FormFlowConstants.FORM_STEP_TRACE_MAP)
    protected Map<String, Object> formTraceMap;

    @ContextData(name = FormFlowConstants.FORM_STEP_TRACE_MAP_STR, scope = Context.SCOPE_DEFAULT)
    protected String formTraceMapStr;

    @ContextData(name = FormFlowConstants.FORM_STEP_RENDER_TARGET)
    protected String renderTargetStep;

    /**
     * Sub class should tell us the current rendering mode. Since we have no any information about the concrete cases, we always return true
     * by default.
     * 
     * @param step
     * @param form
     * @param fieldName
     * @return
     */
    protected boolean renderForEdit(String step, Object form, String fieldName) {
        return true;
    }

    private FieldRenderingInfo getRenderingInfo(AnnotatedPropertyInfo f, int cascadeFormArrayIndex) {
        FieldRenderingInfo info = FieldRenderingInfoMap.get(f);
        if (info == null) {

            info = new FieldRenderingInfo();

            FormField ffAnno = f.getAnnotation(FormField.class);

            String fieldName = f.getName();

            String editSelector = ffAnno.editSelector();
            if (StringUtils.isEmpty(editSelector)) {
                editSelector = defaultEditElementSelectorForField(fieldName);
            }

            info.editSelector = editSelector;

            String displaySelector = ffAnno.displaySelector();
            if (StringUtils.isEmpty(displaySelector)) {
                displaySelector = defaultDisplayElementSelectorForField(fieldName);
            }

            info.displaySelector = displaySelector;

            try {
                info.valueRenderer = ffAnno.fieldValueRenderer().newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }

            if (Configuration.getConfiguration().isCacheEnable()) {
                FieldRenderingInfoMap.put(f, info);
            }
        }
        if (cascadeFormArrayIndex >= 0) {
            return info.replaceArrayIndex(cascadeFormArrayIndex);
        } else {
            return info;
        }
    }

    /**
     * The entry of form rendering. Sub classes could override it in case of necessarily.
     * 
     * @return
     * @throws Exception
     */
    public Renderer render() throws Exception {
        Renderer renderer = renderTraceMapData();
        Object form = retrieveRenderTargetForm();
        return renderer.add(renderForm(renderTargetStep, form, -1));
    }

    /**
     * We only render the form trace map when it exists
     * 
     * @return
     */
    protected Renderer renderTraceMapData() {
        if (StringUtils.isEmpty(formTraceMapStr)) {
            return Renderer.create();
        } else {
            return Renderer.create(":root", new ElementSetter() {
                @Override
                public void set(Element elem) {
                    Element hide = new Element(Tag.valueOf("input"), "");
                    hide.attr("name", FormFlowConstants.FORM_STEP_TRACE_MAP_STR);
                    hide.attr("type", "hidden");
                    hide.attr("value", formTraceMapStr);
                    elem.appendChild(hide);
                }
            });
        }
    }

    protected Object retrieveRenderTargetForm() {
        return formTraceMap.get(renderTargetStep);
    }

    /**
     * 
     * Render the whole given form instance. All the {@link FormFieldPrepareRenderer}s would be invoked here too.
     * 
     * @param renderTargetStep
     * @param form
     * @param cascadeFormArrayIndex
     * @return
     * @throws Exception
     */
    protected Renderer renderForm(String renderTargetStep, Object form, int cascadeFormArrayIndex) throws Exception {
        Renderer render = Renderer.create();
        if (form == null) {
            return render;
        }

        render.disableMissingSelectorWarning();

        List<FormFieldPrepareRenderer> fieldDataPrepareRendererList = retrieveFieldPrepareRenderers(renderTargetStep, form);

        for (FormFieldPrepareRenderer formFieldDataPrepareRenderer : fieldDataPrepareRendererList) {
            FieldRenderingInfo renderingInfo = getRenderingInfo(formFieldDataPrepareRenderer.targetField(), cascadeFormArrayIndex);
            render.add(formFieldDataPrepareRenderer.preRender(renderingInfo.editSelector, renderingInfo.displaySelector));
        }

        render.add(renderValueOfFields(renderTargetStep, form, cascadeFormArrayIndex));

        for (FormFieldPrepareRenderer formFieldDataPrepareRenderer : fieldDataPrepareRendererList) {
            FieldRenderingInfo renderingInfo = getRenderingInfo(formFieldDataPrepareRenderer.targetField(), cascadeFormArrayIndex);
            render.add(formFieldDataPrepareRenderer.postRender(renderingInfo.editSelector, renderingInfo.displaySelector));
        }

        return render.enableMissingSelectorWarning();
    }

    /**
     * 
     * Render the value of all the given form's fields.The rendering of cascade forms will be done here as well(recursively call the
     * {@link #renderForm(String, Object, int)}).
     * 
     * @param renderTargetStep
     * @param form
     * @param cascadeFormArrayIndex
     * @return
     * @throws Exception
     */
    private Renderer renderValueOfFields(String renderTargetStep, Object form, int cascadeFormArrayIndex) throws Exception {
        Renderer render = Renderer.create();
        List<AnnotatedPropertyInfo> fieldList = AnnotatedPropertyUtil.retrieveProperties(form.getClass());

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
                        // retrieve the form instance
                        if (i >= 0) {
                            subForm = Array.get(v, i);
                        } else {
                            // create a template instance
                            subForm = createFormInstanceForCascadeFormArrayTemplate(subFormType);
                        }

                        Renderer subRenderer = Renderer.create();

                        // only rewrite the refs for normal instances
                        if (i >= 0) {
                            // subRenderer.add(setCascadeFormContainerArrayRef(i));
                            subRenderer.add(rewriteCascadeFormFieldArrayRef(renderTargetStep, subForm, i));
                        }

                        subRenderer.add(renderForm(renderTargetStep, subForm, i));

                        // hide the template DOM
                        if (i < 0) {
                            subRenderer.add(":root", hideCascadeFormTemplateDOM(subFormType));
                        }

                        subRendererList.add(subRenderer);
                    }
                    render.add(containerSelector, subRendererList);
                } else {// a simple cascade form

                    if (StringUtils.isNotEmpty(containerSelector)) {
                        render.add(containerSelector, renderForm(renderTargetStep, v, -1));
                    } else {
                        render.add(renderForm(renderTargetStep, v, -1));
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

            FieldRenderingInfo renderingInfo = getRenderingInfo(field, cascadeFormArrayIndex);

            // render.addDebugger("whole form before: " + field.getName());

            if (renderForEdit(renderTargetStep, form, field.getName())) {
                render.add(renderingInfo.valueRenderer.renderForEdit(renderingInfo.editSelector, v));
            } else {
                render.add(renderingInfo.valueRenderer.renderForDisplay(renderingInfo.editSelector, renderingInfo.displaySelector, v));
            }
        }
        return render;
    }

    protected String defaultDisplayElementSelectorForField(String fieldName) {
        return SelectorUtil.id(fieldName + "-display");
    }

    protected String defaultEditElementSelectorForField(String fieldName) {
        return SelectorUtil.attr("name", fieldName);
    }

    protected Renderer hideCascadeFormTemplateDOM(Class<?> subFormType) {
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
    protected Object createFormInstanceForCascadeFormArrayTemplate(Class subFormType) throws InstantiationException, IllegalAccessException {
        return subFormType.newInstance();
    }

    /*
     * It seems that we do not need it?
     * 
     * @param cascadeFormArrayIndex
     * @return
     */
    private Renderer setCascadeFormContainerArrayRef(final int cascadeFormArrayIndex) {
        return Renderer.create(":root", new ElementSetter() {
            @Override
            public void set(Element elem) {
                elem.attr(cascadeFormContainerArrayRefAttrName(), String.valueOf(cascadeFormArrayIndex));
            }
        });
    }

    private String cascadeFormContainerArrayRefAttrName() {
        return "cascade-form-container-array-ref";
    }

    /**
     * Sub classes could override this method to customize how to rewrite the array index for cascade array forms.
     * 
     * @param renderTargetStep
     * @param form
     * @param cascadeFormArrayIndex
     * @return
     */
    protected Renderer rewriteCascadeFormFieldArrayRef(final String renderTargetStep, final Object form, final int cascadeFormArrayIndex) {

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
                        elem.attr(attr, rewriteArrayIndexPlaceHolder(v, cascadeFormArrayIndex));
                    }
                }
            }
        });
    }

    private static final String[] _rewriteCascadeFormFieldArrayRefTargetAttrs = { "id", "name", "cascade-ref", "cascade-ref-target" };

    /**
     * The attributes returned by this method will be rewritten for array index.
     * <p>
     * The default is {"id", "name", "cascade-ref", "cascade-ref-target"}.
     * 
     * @return
     */
    protected String[] rewriteCascadeFormFieldArrayRefTargetAttrs() {
        return _rewriteCascadeFormFieldArrayRefTargetAttrs;
    }

    /**
     * Sub classes can override this method to supply a customized array index placeholder mechanism.
     * 
     * @param s
     * @param seq
     * @return
     * @see AbstractFormFlowHandler#rewriteArrayIndexPlaceHolder(String, int)
     */
    protected String rewriteArrayIndexPlaceHolder(String s, int seq) {
        return CascadeFormUtil.rewriteArrayIndexPlaceHolder(s, seq);
    }

    /**
     * Sub classes should override this method to supply field prepare renderers.
     * 
     * @return
     * @throws Exception
     */
    protected List<FormFieldPrepareRenderer> retrieveFieldPrepareRenderers(String renderTargetStep, Object form) {
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
    protected Object convertRawInjectionTraceDataToRenderingData(String fieldName, Class fieldDataType, Object rawTraceData) {
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

}
