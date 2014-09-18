package com.astamuse.asta4d.web.form.flow.base;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.lang.reflect.Array;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.astamuse.asta4d.Context;
import com.astamuse.asta4d.data.InjectTrace;
import com.astamuse.asta4d.data.InjectUtil;
import com.astamuse.asta4d.util.annotation.AnnotatedPropertyInfo;
import com.astamuse.asta4d.util.annotation.AnnotatedPropertyUtil;
import com.astamuse.asta4d.web.WebApplicationContext;
import com.astamuse.asta4d.web.dispatch.response.provider.RedirectTargetProvider;
import com.astamuse.asta4d.web.form.annotation.CascadeFormField;
import com.astamuse.asta4d.web.form.validation.FormValidationMessage;
import com.astamuse.asta4d.web.form.validation.FormValidator;
import com.astamuse.asta4d.web.form.validation.JsrValidator;
import com.astamuse.asta4d.web.form.validation.TypeUnMatchValidator;
import com.astamuse.asta4d.web.util.message.DefaultMessageRenderingHelper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@SuppressWarnings({ "rawtypes", "unchecked" })
public abstract class AbstractFormFlowHandler<T> {

    private static final ObjectMapper JsonMapper = new ObjectMapper();

    private static final String FORM_PRE_DEFINED = "FORM_PRE_DEFINED#IntelligentFormHandler";

    private static final String FORM_EXTRA_DATA = "FORM_EXTRA_DATA#IntelligentFormHandler";

    private Class<? extends FormProcessData> formProcessDataCls;
    private Class formCls;

    public AbstractFormFlowHandler(Class<T> formCls) {
        this(formCls, SimpleFormProcessData.class);
    }

    public AbstractFormFlowHandler(Class<T> formCls, Class<? extends FormProcessData> formProcessDataCls) {
        this.formCls = formCls;
        this.formProcessDataCls = formProcessDataCls;
    }

    protected abstract T createInitForm();

    protected String handleWithRenderTargetStep() throws Exception {
        return (String) handle(true);
    }

    protected CommonFormResult handleWithCommonFormResult() throws Exception {
        return (CommonFormResult) handle(false);
    }

    protected <D> void saveExtraDataToContext(D actionInfo) {
        Context.getCurrentThreadContext().setData(FORM_EXTRA_DATA, actionInfo);
    }

    protected <D> D getExtraDataFromContext() {
        return Context.getCurrentThreadContext().getData(FORM_EXTRA_DATA);
    }

    private void savePreDefinedForm(T form) {
        Context.getCurrentThreadContext().setData(FORM_PRE_DEFINED, form);
    }

    protected Object handle(boolean returnRenderTargetStep) throws Exception {
        FormProcessData processData = (FormProcessData) InjectUtil.retrieveContextDataSetInstance(formProcessDataCls,
                "not-exist-IntelligentFormProcessData", "");

        if (processData.getStepExit() != null) {
            return null;
        }

        String currentStep = processData.getStepCurrent();

        if (currentStep == null) {// it means the first time access without existing input data
            currentStep = FormFlowConstants.FORM_STEP_INIT_STEP;
            savePreDefinedForm(createInitForm());
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

        String renderTargetStep = null;
        CommonFormResult formResult = null;

        if (processData.getStepBack() != null) {
            renderTargetStep = processData.getStepBack();
            traceMap.remove(currentStep);
            passDataToSnippet(currentStep, renderTargetStep, traceMap, null);
        } else {
            if (FormFlowConstants.FORM_STEP_INIT_STEP.equals(currentStep)) {
                renderTargetStep = FormFlowConstants.FORM_STEP_INIT_STEP;
            } else {
                formResult = handle(currentStep, form);
                if (formResult == CommonFormResult.SUCCESS) {
                    renderTargetStep = processData.getStepSuccess();
                } else {
                    renderTargetStep = processData.getStepFailed();
                }
            }
            passDataToSnippet(currentStep, renderTargetStep, traceMap, formResult);
        }

        if (returnRenderTargetStep) {
            return renderTargetStep;
        } else {
            return formResult;
        }
    }

    protected T retrieveFormInstance(Map<String, Object> traceMap, String currentStep) {
        try {
            final T form = (T) InjectUtil.retrieveContextDataSetInstance(formCls, FORM_PRE_DEFINED, "");
            List<AnnotatedPropertyInfo> list = AnnotatedPropertyUtil.retrieveProperties(formCls);
            Context currentContext = Context.getCurrentThreadContext();
            for (final AnnotatedPropertyInfo field : list) {
                CascadeFormField cff = field.getAnnotation(CascadeFormField.class);
                if (cff != null) {
                    if (field.retrieveValue(form) != null) {
                        continue;
                    }

                    if (StringUtils.isEmpty(cff.arrayLengthField())) {
                        continue;
                    }

                    AnnotatedPropertyInfo arrayLengthField = AnnotatedPropertyUtil.retrievePropertyByName(formCls, cff.arrayLengthField());
                    if (arrayLengthField == null) {
                        throw new NullPointerException("specified array length field [" + cff.arrayLengthField() + "] was not found");
                    }

                    final Integer len = (Integer) arrayLengthField.retrieveValue(form);
                    if (len == null) {
                        throw new NullPointerException("specified array length field [" + cff.arrayLengthField() + "] is null");
                    }

                    final Object[] array = (Object[]) Array.newInstance(field.getType().getComponentType(), len);
                    for (int i = 0; i < len; i++) {
                        final int seq = i;
                        Context.with(new DelatedContext(currentContext, seq), new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Object subform = field.getType().getComponentType().newInstance();
                                    InjectUtil.injectToInstance(subform);
                                    Array.set(array, seq, subform);
                                    // TODO retrieve typeunmatch errors and add array index information
                                    /*
                                    List<AnnotatedPropertyInfo> subPropList = AnnotatedPropertyUtil.retrieveProperties(subform.getClass());
                                    for (AnnotatedPropertyInfo subProp : subPropList) {
                                        ContextDataHolder valueHolder;
                                        if (subProp.getField() != null) {
                                            valueHolder = InjectTrace.getInstanceInjectionTraceInfo(subform, subProp.getField());
                                        } else {
                                            valueHolder = InjectTrace.getInstanceInjectionTraceInfo(subform, subProp.getSetter());
                                        }
                                        if (valueHolder != null) {
                                            String rewriteName = CascadeFormUtil.rewriteFieldName(subProp.getName(), seq);
                                            InjectTrace.saveInstanceInjectionTraceInfo(array, rewriteName, valueHolder);
                                        }
                                    }
                                    */
                                } catch (Exception e) {
                                    throw new RuntimeException(e);
                                }
                            }
                        });

                    }

                    field.assginValue(form, array);
                }
            }
            return form;
        } catch (Exception e) {
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

            RedirectTargetProvider.addFlashScopeData(AbstractFormFlowSnippet.PRE_INJECTION_TRACE_INFO, InjectTrace.retrieveTraceList());

            RedirectTargetProvider.addFlashScopeData(FormFlowConstants.FORM_STEP_TRACE_MAP, traceMap);

            String traceData = serializeTraceMap(traceMap);

            RedirectTargetProvider.addFlashScopeData(FormFlowConstants.FORM_STEP_TRACE_MAP_STR, traceData);

            RedirectTargetProvider.addFlashScopeData(FormFlowConstants.FORM_STEP_RENDER_TARGET, renderTargetStep);
        } else {

            WebApplicationContext context = WebApplicationContext.getCurrentThreadWebApplicationContext();

            context.setData(FormFlowConstants.FORM_STEP_TRACE_MAP, traceMap);

            String traceData = serializeTraceMap(traceMap);

            context.setData(FormFlowConstants.FORM_STEP_TRACE_MAP_STR, traceData);

            context.setData(FormFlowConstants.FORM_STEP_RENDER_TARGET, renderTargetStep);
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
        List<FormValidationMessage> validationMesssages = validate(form);
        if (validationMesssages.isEmpty()) {
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
