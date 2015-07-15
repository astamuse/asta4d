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

import org.apache.commons.lang3.StringUtils;

import com.astamuse.asta4d.web.form.flow.base.BasicFormFlowSnippetTrait;

/**
 * This trait represents the rendering way of a classical flow that contains input/confirm/complete steps.
 * 
 * @author e-ryu
 *
 */
public interface ClassicalMultiStepFormFlowSnippetTrait extends BasicFormFlowSnippetTrait {

    /**
     * 
     * We will treat confirm and complete step as non edit step by default.
     * 
     * <p>
     * From parent:
     * <p>
     * {@inheritDoc}
     * 
     * @return true when the step is confirm/complete by default
     * @see ClassicalMultiStepFormFlowTraitHelper#NonEditSteps
     */
    @Override
    default boolean renderForEdit(String step, Object form, String fieldName) {
        if (StringUtils.isEmpty(step)) {
            return true;
        } else {
            return !ClassicalMultiStepFormFlowTraitHelper.NonEditSteps.contains(step.toLowerCase());
        }
    }
}
