package com.astamuse.asta4d.web.form.flow.ext;

import org.apache.commons.lang3.StringUtils;

import com.astamuse.asta4d.render.Renderer;
import com.astamuse.asta4d.snippet.resolve.PriorRenderMethod;
import com.astamuse.asta4d.web.form.flow.base.FormRenderingData;
import com.astamuse.asta4d.web.form.flow.classical.ClassicalMultiStepFormFlowSnippetTrait;

public interface MultiInputStepFormFlowSnippetTrait extends ClassicalMultiStepFormFlowSnippetTrait {

    @PriorRenderMethod
    default Renderer render(MultiInputStepFormRenderingData renderingData) throws Exception {
        return ClassicalMultiStepFormFlowSnippetTrait.super.render(renderingData);
    }

    default String getRenderTargetStep(FormRenderingData renderingData) {
        String formStep = ((MultiInputStepFormRenderingData) renderingData).getFormStep();
        if (StringUtils.isEmpty(formStep)) {
            throw new IllegalArgumentException("form-step must be specified on snippet declaration");
        } else {
            return formStep;
        }
    }

    @Override
    default Object retrieveRenderTargetForm(FormRenderingData renderingData) {
        MultiInputStepForm form = (MultiInputStepForm) ClassicalMultiStepFormFlowSnippetTrait.super.retrieveRenderTargetForm(renderingData);
        String formStep = getRenderTargetStep(renderingData);
        try {
            Object targetForm = form.getSubInputFormByStep(formStep);
            if (targetForm == null) {
                throw new NullPointerException();
            } else {
                return targetForm;
            }
        } catch (Exception ex) {
            throw new RuntimeException("Could not retreive render target form by given form step:" + formStep, ex);
        }

    }
}
