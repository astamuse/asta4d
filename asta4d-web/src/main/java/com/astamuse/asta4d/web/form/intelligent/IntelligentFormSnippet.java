package com.astamuse.asta4d.web.form.intelligent;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Tag;

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
import com.astamuse.asta4d.web.form.field.FormFieldUtil;

public abstract class IntelligentFormSnippet implements InitializableSnippet {

    public static final String PRE_INJECTION_TRACE_INFO = "PRE_INJECTION_TRACE_INFO#IntelligentFormSnippetBase";

    public static final String RENDER_FOR_EDIT = "RENDER_FOR_EDIT#IntelligentFormSnippetBase";

    @ContextData(name = IntelligentFormHandler.FORM_STEP_TRACE_MAP)
    protected Map<String, Object> formTraceMap;

    @ContextData(name = IntelligentFormHandler.FORM_STEP_TRACE_MAP_STR, scope = Context.SCOPE_DEFAULT)
    protected String formTraceMapStr;

    protected boolean renderForEdit(String step) {
        return true;
    }

    @Override
    public void init() throws SnippetInvokeException {
        List list = (List) Context.getCurrentThreadContext().getData(WebApplicationContext.SCOPE_FLASH, PRE_INJECTION_TRACE_INFO);
        InjectTrace.restoreTraceList(list);
    }

    public Renderer render(@ContextData(name = IntelligentFormHandler.FORM_STEP_RENDER_TARGET) String renderTargetStep) throws Exception {
        Renderer renderer = Renderer.create("form", new ElementSetter() {
            @Override
            public void set(Element elem) {
                Element hide = new Element(Tag.valueOf("input"), "");
                hide.attr("name", IntelligentFormHandler.FORM_STEP_TRACE_MAP_STR);
                hide.attr("type", "hidden");
                hide.attr("value", formTraceMapStr);
                elem.appendChild(hide);
            }
        });

        Object form = formTraceMap.get(renderTargetStep);

        return renderer.add(renderFieldSupportData(renderTargetStep, form)).add(renderFieldValue(renderTargetStep, form));
    }

    protected Renderer renderFieldValue(String renderTargetStep, Object form) throws Exception {
        Renderer render = Renderer.create();
        if (form == null) {
            return render;
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

            FormField ffAnno = ConvertableAnnotationRetriever.retrieveAnnotation(FormField.class, field.getAnnotations());

            String fieldName = ffAnno.name();
            if (StringUtils.isEmpty(fieldName)) {
                fieldName = field.getName();
            }

            String editSelector = ffAnno.editSelector();
            if (StringUtils.isEmpty(editSelector)) {
                editSelector = "[name=" + fieldName + "]";
            }

            String displaySelector = ffAnno.displaySelector();
            if (StringUtils.isEmpty(displaySelector)) {
                displaySelector = displayElementSelectorForField(fieldName);
            }

            render.addDebugger("whole form before: " + fieldName);

            if (renderForEdit(renderTargetStep)) {
                render.add(ffAnno.fieldValueRenderer().newInstance().renderForEdit(editSelector, v));
            } else {
                render.add(ffAnno.fieldValueRenderer().newInstance().renderForDisplay(editSelector, displaySelector, v));
            }

        }
        return render;
    }

    protected String displayElementSelectorForField(String fieldName) {
        return "#" + fieldName + "-disply";
    }

    /**
     * should be overridden
     * 
     * @return
     * @throws Exception
     */
    protected Renderer renderFieldSupportData(String renderTargetStep, Object form) throws Exception {
        Renderer render = Renderer.create();
        return render;
    }

}
