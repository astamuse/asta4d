package com.astamuse.asta4d.web.form.flow.ext;

import java.util.Map;

import com.astamuse.asta4d.web.form.flow.base.FormFlowConstants;
import com.astamuse.asta4d.web.form.flow.base.FormFlowTraceData;
import com.astamuse.asta4d.web.form.flow.classical.ClassicalMultiStepFormFlowHandlerTrait;

/**
 * For a form flow with multiple input steps, there are something different from the classical single input step form flow:
 * <ul>
 * <li>We store the trace data from the first step for later using
 * <li>We set the initial form data for each input step by copying the initial step(before first) form data
 * <li>We combine the form data of each step to one single instance for confirm page rendering and later process
 * <li>We validate the corresponding sub form instance of each input step rather than the parent form instance
 * </ul>
 * 
 * @author e-ryu
 *
 * @param <T>
 */
public interface MultiInputStepFormFlowHandlerTrait<T extends MultiInputStepForm> extends ClassicalMultiStepFormFlowHandlerTrait<T> {

    public String[] getInputSteps();

    default String firstStepName() {
        return getInputSteps()[0];
    };

    @Override
    default boolean skipStoreTraceData(String currentStep, String renderTargetStep, FormFlowTraceData traceData) {
        // we will always save trace data when we start the flow because we need to retrieve the init form later.
        return completeStepName().equalsIgnoreCase(renderTargetStep);
    }

    /**
     * get the form instance which combined the input data of all the steps. Default is combine all the data of each steps to the first
     * input step instance.
     * 
     * @param traceData
     * @return
     */

    @SuppressWarnings("unchecked")
    default void mergeInputStepsToConfirmStep(T confirmStepForm, FormFlowTraceData traceData) {
        Map<String, Object> formMap = traceData.getStepFormMap();
        String[] inputSteps = getInputSteps();
        T mergeForm;
        String step;
        for (int i = 0; i < inputSteps.length; i++) {
            step = inputSteps[i];
            mergeForm = (T) formMap.get(step);
            confirmStepForm.mergeInputDataForConfirm(step, mergeForm);
        }
    }

    /**
     * get the
     * 
     * @param traceData
     * @return
     */
    @SuppressWarnings("unchecked")
    default T getConfirmStepFormStub(FormFlowTraceData traceData) {
        // clone or not, we don't matter
        Map<String, Object> formMap = traceData.getStepFormMap();
        return (T) formMap.get(FormFlowConstants.FORM_STEP_BEFORE_FIRST);
    }

    @SuppressWarnings("unchecked")
    @Override
    default void rewriteTraceDataBeforeGoSnippet(String currentStep, String renderTargetStep, FormFlowTraceData traceData) {
        Map<String, Object> formMap = traceData.getStepFormMap();
        if (confirmStepName().equalsIgnoreCase(renderTargetStep)) {// ? -> confirm
            T confirmStepForm = getConfirmStepFormStub(traceData);
            mergeInputStepsToConfirmStep(confirmStepForm, traceData);
            formMap.put(confirmStepName(), confirmStepForm);
        } else if (completeStepName().equalsIgnoreCase(renderTargetStep)) { // confirm -> complete
            formMap.put(renderTargetStep, formMap.get(currentStep));
        } else {// input x -> input y
            // in this else branch, we could believe that the render target must be an input step
            /*
             * we need to set the form data by the stored before first step data for each step when the form data is not set, 
             * otherwise we use the existing form data.
             * 
             * we forced to store the before first step data by return false at the method of skipStoreTraceData.
             */
            T savedForm = (T) formMap.get(renderTargetStep);
            if (savedForm == null) {
                // There must be a init form
                T initForm = (T) formMap.get(FormFlowConstants.FORM_STEP_BEFORE_FIRST);
                formMap.put(renderTargetStep, initForm);
            }
        }

    }

}
