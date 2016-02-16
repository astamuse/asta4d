/*
 * Copyright 2016 astamuse company,Ltd.
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
package com.astamuse.asta4d.web.form.flow.base;

/**
 * This interface suggests the validation process should validate the retrieved target rather than the form instance itself.
 * 
 * @author e-ryu
 *
 */
public interface StepAwaredValidatableForm {

    /**
     * The default implementation of this method is to retrieve the field annotated by {@link StepAwaredValidationTarget}.
     * 
     * @param step
     * @return
     */
    default Object getValidationTarget(String step) {
        return StepAwaredValidationFormHelper.getValidationTargetByAnnotation(this, step);
    }
}
