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

import java.util.Map;

import com.astamuse.asta4d.web.form.flow.base.BasicFormFlowHandlerTrait;
import com.astamuse.asta4d.web.form.flow.base.FormProcessData;

/**
 * 
 * This trait represents a classical one step form flow which contains only one input step.
 * 
 * @author e-ryu
 *
 * @param <T>
 */
public interface OneStepFormHandlerTrait<T> extends ClassicalMultiStepFormFlowHandlerTrait<T> {

    /**
     * The default path var name of template file path
     */
    public static final String VAR_INPUT_TEMPLATE_FILE = VAR_TEMPLATE_BASE_PATH;

    /**
     * for a one step form, we will always do update after validation succeed.
     */
    @Override
    default boolean doUpdateOnValidationSuccess(FormProcessData processData) {
        return true;
    }

    /**
     * for a one step form, we will always treat the complete step as exit
     */
    @Override
    default boolean treatCompleteStepAsExit() {
        return true;
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
    default boolean skipSaveTraceMap(String currentStep, String renderTargetStep, Map<String, Object> traceMap) {
        return true;
    }

    /**
     * for a one step form, we will always return the configured template path
     */
    default String createTemplateFilePath(String templateBasePath, String step) {
        return templateBasePath;
    }
}
