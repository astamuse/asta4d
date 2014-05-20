package com.astamuse.asta4d.web.form;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.astamuse.asta4d.Context;
import com.astamuse.asta4d.data.ContextDataSetFactory;
import com.astamuse.asta4d.data.DataOperationException;
import com.astamuse.asta4d.data.InjectTrace;
import com.astamuse.asta4d.data.InjectUtil;
import com.astamuse.asta4d.data.annotation.ContextDataSet;
import com.astamuse.asta4d.util.annotation.ConvertableAnnotationRetriever;
import com.astamuse.asta4d.web.WebApplicationContext;
import com.astamuse.asta4d.web.dispatch.response.provider.RedirectTargetProvider;
import com.astamuse.asta4d.web.util.message.DefaultMessageRenderingHelper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@SuppressWarnings({ "rawtypes", "unchecked" })
public abstract class IntelligentFormHandler<T> {

    private static final ObjectMapper JsonMapper = new ObjectMapper();

    public static final String FORM_STEP_TRACE_MAP = "FORM_STEP_TRACE_MAP#IntelligentFormHandler";

    public static final String FORM_STEP_TRACE_MAP_STR = "FORM_STEP_TRACE_MAP_STR#IntelligentFormHandler";

    public static final String FORM_STEP_RENDER_TARGET = "form-step";

    public static final String FORM_STEP_INIT_STEP = "FORM_STEP_INIT_STEP#IntelligentFormHandler";

    private static final String FORM_PRE_DEFINED = "FORM_PRE_DEFINED#IntelligentFormHandler";

    private Class<? extends IntelligentFormProcessData> formProcessDataCls;
    private Class formCls;
    private ContextDataSetFactory formFactory;

    public IntelligentFormHandler(Class<T> formCls) {
        this(formCls, SimpleFormProcessData.class);
    }

    public IntelligentFormHandler(Class<T> formCls, Class<? extends IntelligentFormProcessData> formProcessDataCls) {
        this.formCls = formCls;
        this.formProcessDataCls = formProcessDataCls;

        ContextDataSet cds = ConvertableAnnotationRetriever.retrieveAnnotation(ContextDataSet.class, formCls.getAnnotations());
        try {
            formFactory = cds.factory().newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    protected void savePreDefinedForm(T form) {
        Context.getCurrentThreadContext().setData(FORM_PRE_DEFINED, form);
    }

    protected String handleWithRenderTargetResult() throws Exception {
        return (String) handle(true);
    }

    protected CommonFormResult handleWithCommonFormResult() throws Exception {
        return (CommonFormResult) handle(false);
    }

    protected Object handle(boolean returnRenderTarget) throws Exception {
        IntelligentFormProcessData processData = (IntelligentFormProcessData) InjectUtil.retrieveContextDataSetInstance(formProcessDataCls,
                "not-exist-IntelligentFormProcessData", "");

        String currentStep = processData.getStepCurrent();

        if (currentStep == null) {// it means the first time access without existing input data
            currentStep = FORM_STEP_INIT_STEP;
        } else {
        }

        String traceData = processData.getStepTraceData();

        Map<String, Object> traceMap;

        if (StringUtils.isEmpty(traceData)) {
            traceMap = new HashMap<>();
        } else {
            traceMap = deserializeTraceMap(traceData);
        }

        T form = retrieveFormInstance(traceMap, currentStep);

        traceMap.put(currentStep, form);

        String renderTarget = null;
        CommonFormResult formResult = null;

        if (processData.getStepBack() != null) {
            renderTarget = processData.getStepBack();
            traceMap.remove(currentStep);
            passDataToSnippet(currentStep, renderTarget, traceMap, null);
        } else {
            if (FORM_STEP_INIT_STEP.equals(currentStep)) {
                renderTarget = FORM_STEP_INIT_STEP;
            } else {
                formResult = handle(currentStep, form);
                if (formResult == CommonFormResult.SUCCESS) {
                    renderTarget = processData.getStepSuccess();
                } else {
                    renderTarget = processData.getStepFailed();
                }
            }
            passDataToSnippet(currentStep, renderTarget, traceMap, formResult);
        }

        if (returnRenderTarget) {
            return renderTarget;
        } else {
            return formResult;
        }
    }

    protected T retrieveFormInstance(Map<String, Object> traceMap, String currentStep) {
        try {
            if (FORM_STEP_INIT_STEP.equals(currentStep)) {
                return (T) formFactory.createInstance(formCls);
            } else {
                return (T) InjectUtil.retrieveContextDataSetInstance(formCls, FORM_PRE_DEFINED, "");
            }
        } catch (DataOperationException e) {
            throw new RuntimeException(e);
        }
    }

    protected String serializeTraceMap(Map<String, Object> traceMap) {
        try {
            Map<String, byte[]> rawMap = new HashMap<>();
            for (Map.Entry<String, Object> traceEntry : traceMap.entrySet()) {
                Object form = traceEntry.getValue();
                ByteArrayOutputStream bos = new ByteArrayOutputStream(1000);

                byte[] clsName = form.getClass().getName().getBytes();
                byte[] lenBytes = ByteBuffer.allocate(4).putInt(clsName.length).array();

                bos.write(lenBytes);
                bos.write(clsName);
                JsonMapper.writeValue(bos, form);

                rawMap.put(traceEntry.getKey(), bos.toByteArray());

            }
            return JsonMapper.writeValueAsString(rawMap);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected Map<String, Object> deserializeTraceMap(String s) {
        try {
            Map<String, byte[]> rawMap = JsonMapper.readValue(s, new TypeReference<HashMap<String, byte[]>>() {
            });
            Map<String, Object> traceMap = new HashMap<>();
            for (Map.Entry<String, byte[]> rawEntry : rawMap.entrySet()) {
                byte[] data = rawEntry.getValue();
                ByteBuffer bb = ByteBuffer.wrap(data);

                int clsNameLen = bb.getInt();

                String clsName = new String(data, 4, clsNameLen);

                ByteArrayInputStream bis = new ByteArrayInputStream(data, 4 + clsNameLen, data.length - 4 - clsNameLen);
                Object form = JsonMapper.readValue(bis, Class.forName(clsName));
                traceMap.put(rawEntry.getKey(), form);
            }
            return traceMap;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    protected void passDataToSnippet(String currentStep, String renderTargetStep, Map<String, Object> traceMap, CommonFormResult result) {
        T form = (T) traceMap.get(currentStep);
        if (passDataToSnippetByFlash(currentStep, renderTargetStep, form, result)) {
            DefaultMessageRenderingHelper msgHelper = DefaultMessageRenderingHelper.instance();
            msgHelper.saveMessageListToFlash();

            RedirectTargetProvider.addFlashScopeData(IntelligentFormSnippet.PRE_INJECTION_TRACE_INFO, InjectTrace.retrieveTraceList());

            RedirectTargetProvider.addFlashScopeData(FORM_STEP_TRACE_MAP, traceMap);

            String traceData = serializeTraceMap(traceMap);

            RedirectTargetProvider.addFlashScopeData(FORM_STEP_TRACE_MAP_STR, traceData);

            RedirectTargetProvider.addFlashScopeData(FORM_STEP_RENDER_TARGET, renderTargetStep);
        } else {

            WebApplicationContext context = WebApplicationContext.getCurrentThreadWebApplicationContext();

            context.setData(FORM_STEP_TRACE_MAP, traceMap);

            String traceData = serializeTraceMap(traceMap);

            context.setData(FORM_STEP_TRACE_MAP_STR, traceData);

            context.setData(FORM_STEP_RENDER_TARGET, renderTargetStep);
        }
    }

    protected boolean passDataToSnippetByFlash(String currentStep, String renderTargetStep, T form, CommonFormResult result) {
        return false;
    }

    protected FormValidator getTypeUnMatchValidator() {
        return new TypeUnMatchValidator();
    }

    protected FormValidator getValueValidator() {
        return new JsrValidator();
    }

    protected List<FormValidationMessage> validate(T form) {
        List<FormValidationMessage> validationMessages = new LinkedList<>();

        validationMessages.addAll(getTypeUnMatchValidator().validate(form));
        if (!validationMessages.isEmpty()) {
            return validationMessages;
        }

        validationMessages.addAll(getValueValidator().validate(form));
        return validationMessages;
    }

    protected CommonFormResult handle(String currentStep, T form) {
        WebApplicationContext context = Context.getCurrentThreadContext();

        List<FormValidationMessage> validationMesssages = validate(form);
        if (validationMesssages.isEmpty()) {
            context.setData(IntelligentFormSnippet.RENDER_FOR_EDIT, false);
            return CommonFormResult.SUCCESS;
        } else {
            DefaultMessageRenderingHelper msgHelper = DefaultMessageRenderingHelper.instance();
            for (FormValidationMessage formValidationMessage : validationMesssages) {
                msgHelper.err("#" + formValidationMessage.getName() + "-err-msg", formValidationMessage.getMessage());
            }

            return CommonFormResult.FAILED;
        }
    }
}
