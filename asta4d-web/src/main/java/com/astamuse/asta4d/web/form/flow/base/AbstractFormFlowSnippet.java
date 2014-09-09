package com.astamuse.asta4d.web.form.flow.base;

import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
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
import com.astamuse.asta4d.util.annotation.ConvertableAnnotationRetriever;
import com.astamuse.asta4d.web.WebApplicationContext;
import com.astamuse.asta4d.web.form.annotation.FormField;
import com.astamuse.asta4d.web.form.field.FormFieldAdditionalRenderer;
import com.astamuse.asta4d.web.form.field.FormFieldUtil;
import com.astamuse.asta4d.web.form.field.FormFieldValueRenderer;

public abstract class AbstractFormFlowSnippet implements InitializableSnippet {

    private static class FieldRenderingInfo {
        String editSelector;
        String displaySelector;
        FormFieldValueRenderer valueRenderer;
    }

    private static final Map<Field, FieldRenderingInfo> FieldRenderingInfoMap = new ConcurrentHashMap<>();

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

    private FieldRenderingInfo getRenderingInfo(Field f) {
        FieldRenderingInfo info = FieldRenderingInfoMap.get(f);
        if (info == null) {

            FormField ffAnno = ConvertableAnnotationRetriever.retrieveAnnotation(FormField.class, f.getAnnotations());
            if (ffAnno == null) {
                throw new RuntimeException(f + " is not annotated by @FormField");
            }

            info = new FieldRenderingInfo();

            String fieldName = ffAnno.name();
            if (StringUtils.isEmpty(fieldName)) {
                fieldName = f.getName();
            }

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
        return info;
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

        return renderer.add(renderFieldValue(renderTargetStep, form));
    }

    protected Renderer renderFieldValue(String renderTargetStep, Object form) throws Exception {
        Renderer render = Renderer.create();
        if (form == null) {
            return render;
        }

        render.disableMissingSelectorWarning();

        List<FormFieldAdditionalRenderer> fieldAdditionalRendererList = retrieveFieldAdditionalRenderer(renderTargetStep, form);

        for (FormFieldAdditionalRenderer formFieldAdditionalRenderer : fieldAdditionalRendererList) {
            FieldRenderingInfo renderingInfo = getRenderingInfo(formFieldAdditionalRenderer.targetField());
            render.add(formFieldAdditionalRenderer.preRender(renderingInfo.editSelector, renderingInfo.displaySelector));
        }

        List<Field> fieldList = FormFieldUtil.retrieveFormFields(form.getClass());

        for (Field field : fieldList) {
            Object v = FieldUtils.readField(field, form, true);

            // TODO wrong rendering for null
            if (v == null) {
                @SuppressWarnings("rawtypes")
                ContextDataHolder valueHolder = InjectTrace.getInstanceInjectionTraceInfo(form, field);
                if (valueHolder != null) {
                    v = valueHolder.getFoundOriginalData();
                }
            }

            FieldRenderingInfo renderingInfo = getRenderingInfo(field);

            // render.addDebugger("whole form before: " + field.getName());

            if (renderForEdit(renderTargetStep, field.getName())) {
                render.add(renderingInfo.valueRenderer.renderForEdit(renderingInfo.editSelector, v));
            } else {
                render.add(renderingInfo.valueRenderer.renderForDisplay(renderingInfo.editSelector, renderingInfo.displaySelector, v));
            }

        }

        for (FormFieldAdditionalRenderer formFieldAdditionalRenderer : fieldAdditionalRendererList) {
            FieldRenderingInfo renderingInfo = getRenderingInfo(formFieldAdditionalRenderer.targetField());
            render.add(formFieldAdditionalRenderer.postRender(renderingInfo.editSelector, renderingInfo.displaySelector));
        }

        return render;
    }

    protected String displayElementSelectorForField(String fieldName) {
        return "#" + fieldName + "-display";
    }

    /**
     * should be overridden
     * 
     * @return
     * @throws Exception
     */
    protected List<FormFieldAdditionalRenderer> retrieveFieldAdditionalRenderer(String renderTargetStep, Object form) {
        return new LinkedList<>();
    }

}
