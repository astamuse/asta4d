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
package com.astamuse.asta4d.sample.handler.form.common;

import com.astamuse.asta4d.web.form.flow.classical.ClassicalMultiStepFormFlowHandlerTrait;
import com.astamuse.asta4d.web.form.validation.FormValidator;

// @ShowCode:showCommonFormHandlerStart
/**
 * A common parent handler to configure the common actions of form flow process in application. <br>
 * For quick start, an empty class body would be good enough. You only need to do the customization when you really need to do it!!!
 * 
 */
public abstract class Asta4DSamplePrjCommonFormHandler<T> implements ClassicalMultiStepFormFlowHandlerTrait<T> {

    // we use a field to store a pre generated instance rather than create it at every time
    private static SamplePrjTypeUnMatchValidator TypeValidator = new SamplePrjTypeUnMatchValidator(false);

    // as the same as type validator, we cache the value validator instance here
    private static SamplePrjValueValidator ValueValidator = new SamplePrjValueValidator(false);

    @Override
    public FormValidator getTypeUnMatchValidator() {
        return TypeValidator;
    }

    @Override
    public FormValidator getValueValidator() {
        return ValueValidator;
    }

}
// @ShowCode:showCommonFormHandlerEnd