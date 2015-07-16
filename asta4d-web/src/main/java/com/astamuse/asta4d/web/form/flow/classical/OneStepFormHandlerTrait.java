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
package com.astamuse.asta4d.web.form.flow.classical;

import com.astamuse.asta4d.Context;
import com.astamuse.asta4d.web.WebApplicationContext;
import com.astamuse.asta4d.web.dispatch.request.RequestHandler;
import com.astamuse.asta4d.web.form.flow.base.BasicFormFlowHandlerTrait;
import com.astamuse.asta4d.web.form.flow.base.FormFlowTraceData;

/**
 * 
 * This trait represents a classical one step form flow which contains only one input step. The developers are required to at least
 * implement the following methods:
 * <ul>
 * <li>{@link #getFormCls()}
 * <li>{@link #createInitForm()}
 * <li>{@link #updateForm(Object)}
 * </ul>
 * 
 * The target template file for input could be specified in the URL rule by the fixed name of {@link #VAR_INPUT_TEMPLATE_FILE}.
 * 
 * <p>
 * 
 * Also, {@link #getTypeUnMatchValidator()} and {@link #getValueValidator()} are recommended to be overridden by a common parent class to
 * perform validator configuration.
 * 
 * @author e-ryu
 *
 * @param <T>
 */
public interface OneStepFormHandlerTrait<T> extends UpdatableFormFlowHandlerTrait<T> {

    /**
     * The default path var name of template file path
     */
    public static final String VAR_INPUT_TEMPLATE_FILE = "VAR_INPUT_TEMPLATE_FILE#" + OneStepFormHandlerTrait.class.getName();

    /**
     * @see ClassicalFormFlowConstant#STEP_INPUT
     */
    @Override
    default String firstStepName() {
        return ClassicalFormFlowConstant.STEP_INPUT;
    }

    /**
     * In the parent class {@link BasicFormFlowHandlerTrait}'s implementation of skipSaveTraceMap, it says that the sub class have the
     * responsibility to make sure save the trace map well, thus we override it to perform the obligation.
     * 
     * <p>
     * 
     * The trace map will never be saved for a one step form since there is no necessary to keep the trace
     * 
     * @param currentStep
     * @param renderTargetStep
     * @param traceMap
     * @return
     */
    default boolean skipStoreTraceData(String currentStep, String renderTargetStep, FormFlowTraceData traceData) {
        return true;
    }

    /**
     * 
     * Do nothing for clear action since we will never store trace map for a one step form flow
     * 
     */
    @Override
    default void clearStoredTraceData(String traceId) {
        // do nothing
    }

    /**
     * always return null since we will never store trace map for a one step form flow
     */
    @Override
    default FormFlowTraceData retrieveTraceData(String traceId) {
        return null;
    }

    /**
     * By default, the returned result from parent's handle will be translated to the given input template file path.
     * 
     * <p>
     * 
     * From parent:
     * <p>
     * {@inheritDoc}
     * 
     * @see #createTemplateFilePathForStep(String)
     * 
     */
    @Override
    @RequestHandler
    default String handle() throws Exception {
        return createTemplateFilePathForStep(UpdatableFormFlowHandlerTrait.super.handle());
    }

    /**
     * Sub classes can override this method to customize how to translate a step to a target template file path.
     * 
     * @param step
     * @return
     */
    default String createTemplateFilePathForStep(String step) {
        // always exit the flow except the target step is the first step
        if (firstStepName().equals(step)) {
            return getInputTemplateFilePath();
        } else {
            return null;
        }
    }

    /**
     * How to retrieve the target input template file path. The default is retrieving from path var scope by fixed var name
     * {@link #VAR_INPUT_TEMPLATE_FILE}.
     * 
     * @return
     */
    default String getInputTemplateFilePath() {
        return Context.getCurrentThreadContext().getData(WebApplicationContext.SCOPE_PATHVAR, VAR_INPUT_TEMPLATE_FILE);
    }
}
