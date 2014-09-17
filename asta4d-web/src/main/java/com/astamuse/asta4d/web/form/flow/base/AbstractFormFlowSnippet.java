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
import com.astamuse.asta4d.snippet.InitializableSnippet;
import com.astamuse.asta4d.snippet.SnippetInvokeException;
import com.astamuse.asta4d.util.annotation.AnnotatedPropertyInfo;
import com.astamuse.asta4d.util.annotation.AnnotatedPropertyUtil;
import com.astamuse.asta4d.web.WebApplicationContext;
import com.astamuse.asta4d.web.form.annotation.CascadeFormField;
import com.astamuse.asta4d.web.form.annotation.FormField;
import com.astamuse.asta4d.web.form.field.FormFieldDataPrepareRenderer;
import com.astamuse.asta4d.web.form.field.FormFieldValueRenderer;

public abstract class AbstractFormFlowSnippet implements InitializableSnippet {

    private static class FieldRenderingInfo {
        String editSelector;
        String displaySelector;
        FormFieldValueRenderer valueRenderer;

        FieldRenderingInfo replaceArrayIndex(int index) {
            FieldRenderingInfo newInfo = new FieldRenderingInfo();
            String rep = String.valueOf(index);
            newInfo.editSelector = StringUtils.replace(editSelector, "@", rep);
            newInfo.displaySelector = StringUtils.replace(displaySelector, "@", rep);
            newInfo.valueRenderer = valueRenderer;
            return newInfo;
        }
    }

    private static final Map<AnnotatedPropertyInfo, FieldRenderingInfo> FieldRenderingInfoMap = new ConcurrentHashMap<>();

    public static final String PRE_INJECTION_TRACE_INFO = "PRE_INJECTION_TRACE_INFO#IntelligentFormSnippetBase";

    @ContextData(name = FormFlowConstants.FORM_STEP_TRACE_MAP)
    protected Map<String, Object> formTraceMap;

    @ContextData(name = FormFlowConstants.FORM_STEP_TRACE_MAP_STR, scope = Context.SCOPE_DEFAULT)
    protected String formTraceMapStr;

    protected boolean renderForEdit(String step, String fieldName) {
        return true;
    }

    @Override
    public void init() throws SnippetInvokeException {
        List list = (List) Context.getCurrentThreadContext().getData(WebApplicationContext.SCOPE_FLASH, PRE_INJECTION_TRACE_INFO);
        InjectTrace.restoreTraceList(list);
    }

    private FieldRenderingInfo getRenderingInfo(AnnotatedPropertyInfo f, int cascadeFormArrayIndex) {
        FieldRenderingInfo info = FieldRenderingInfoMap.get(f);
        if (info == null) {

            info = new FieldRenderingInfo();

            FormField ffAnno = f.getAnnotation(FormField.class);

            String fieldName = f.getName();

            String editSelector = ffAnno.editSelector();
            if (StringUtils.isEmpty(editSelector)) {
                editSelector = "[name=" + fieldName + "]";
            }

            info.editSelector = editSelector;

            String displaySelector = ffAnno.displaySelector();
            if (StringUtils.isEmpty(displaySelector)) {
                displaySelector = displayElementSelectorForField(fieldName);
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

    public Renderer render(@ContextData(name = FormFlowConstants.FORM_STEP_RENDER_TARGET) String renderTargetStep) throws Exception {
        Renderer renderer = Renderer.create("form", new ElementSetter() {
            @Override
            public void set(Element elem) {
                Element hide = new Element(Tag.valueOf("input"), "");
                hide.attr("name", FormFlowConstants.FORM_STEP_TRACE_MAP_STR);
                hide.attr("type", "hidden");
                hide.attr("value", formTraceMapStr);
                elem.appendChild(hide);
            }
        });

        Object form = formTraceMap.get(renderTargetStep);

        return renderer.add(renderFieldValue(renderTargetStep, form, -1));
    }

    protected Renderer renderFieldValue(String renderTargetStep, Object form, int cascadeFormArrayIndex) throws Exception {
        Renderer render = Renderer.create();
        if (form == null) {
            return render;
        }

        render.disableMissingSelectorWarning();

        List<FormFieldDataPrepareRenderer> fieldDataPrepareRendererList = retrieveFieldDataPrepareRenderer(renderTargetStep, form);

        for (FormFieldDataPrepareRenderer formFieldDataPrepareRenderer : fieldDataPrepareRendererList) {
            FieldRenderingInfo renderingInfo = getRenderingInfo(formFieldDataPrepareRenderer.targetField(), cascadeFormArrayIndex);
            render.add(formFieldDataPrepareRenderer.preRender(renderingInfo.editSelector, renderingInfo.displaySelector));
        }

        render.add(renderValues(renderTargetStep, form, cascadeFormArrayIndex));

        for (FormFieldDataPrepareRenderer formFieldDataPrepareRenderer : fieldDataPrepareRendererList) {
            FieldRenderingInfo renderingInfo = getRenderingInfo(formFieldDataPrepareRenderer.targetField(), cascadeFormArrayIndex);
            render.add(formFieldDataPrepareRenderer.postRender(renderingInfo.editSelector, renderingInfo.displaySelector));
        }

        return render;
    }

    private Renderer renderValues(String renderTargetStep, Object form, int cascadeFormArrayIndex) throws Exception {
        Renderer render = Renderer.create();
        List<AnnotatedPropertyInfo> fieldList = AnnotatedPropertyUtil.retrieveProperties(form.getClass());

        for (AnnotatedPropertyInfo field : fieldList) {

            Object v = field.retrieveValue(form);

            CascadeFormField cff = field.getAnnotation(CascadeFormField.class);
            if (cff != null) {
                String containerSelector = cff.containerSelector();

                if (field.getType().isArray()) {
                    int len = Array.getLength(v);
                    List<Renderer> subRendererList = new ArrayList<>(len);
                    for (int i = 0; i < len; i++) {
                        Object subForm = Array.get(v, i);
                        Renderer subRenderer = rewriteCascadeFormArrayFieldsRef(renderTargetStep, subForm, i);
                        subRendererList.add(subRenderer.add(renderFieldValue(renderTargetStep, subForm, i)));
                    }
                    render.add(containerSelector, subRendererList);
                } else {

                    if (StringUtils.isNotEmpty(containerSelector)) {
                        render.add(containerSelector, renderFieldValue(renderTargetStep, v, -1));
                    } else {
                        render.add(renderFieldValue(renderTargetStep, v, -1));
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
                    v = convertRawTraceDataToRenderingData(field.getName(), field.getType(), valueHolder.getFoundOriginalData());
                }
            }

            FieldRenderingInfo renderingInfo = getRenderingInfo(field, cascadeFormArrayIndex);

            // render.addDebugger("whole form before: " + field.getName());

            if (renderForEdit(renderTargetStep, field.getName())) {
                render.add(renderingInfo.valueRenderer.renderForEdit(renderingInfo.editSelector, v));
            } else {
                render.add(renderingInfo.valueRenderer.renderForDisplay(renderingInfo.editSelector, renderingInfo.displaySelector, v));
            }
        }
        return render;
    }

    protected String displayElementSelectorForField(String fieldName) {
        return "#" + fieldName + "-display";
    }

    protected Renderer rewriteCascadeFormArrayFieldsRef(final String renderTargetStep, final Object form, final int cascadeFormArrayIndex) {
        return Renderer.create("[id],[name]", new ElementSetter() {
            @Override
            public void set(Element elem) {
                String id = elem.id();
                String name = elem.attr("name");
                String rep = String.valueOf(cascadeFormArrayIndex);
                if (StringUtils.isNotEmpty(id)) {
                    elem.attr("id", StringUtils.replace(id, "@", rep));
                }
                if (StringUtils.isNotEmpty(name)) {
                    elem.attr("name", StringUtils.replace(name, "@", rep));
                }
            }
        });
    }

    /**
     * should be overridden
     * 
     * @return
     * @throws Exception
     */
    protected List<FormFieldDataPrepareRenderer> retrieveFieldDataPrepareRenderer(String renderTargetStep, Object form) {
        return new LinkedList<>();
    }

    protected Object convertRawTraceDataToRenderingData(String fieldName, Class fieldDataType, Object rawTraceData) {
        if (fieldDataType.isArray() && rawTraceData.getClass().isArray()) {
            return rawTraceData;
        } else if (rawTraceData.getClass().isArray()) {// but field data type is not array
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
