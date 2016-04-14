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
package com.astamuse.asta4d.web.form.flow.classical;

import org.slf4j.LoggerFactory;

import com.astamuse.asta4d.web.form.flow.base.BasicFormFlowHandlerTrait;
import com.astamuse.asta4d.web.form.flow.base.CommonFormResult;
import com.astamuse.asta4d.web.form.flow.base.FormProcessData;

public interface UpdatableFormFlowHandlerTrait<T> extends BasicFormFlowHandlerTrait<T> {
    /**
     * 
     * This method will call {@link #updateForm(Object)} when the validation is success and the
     * {@link #doUpdateOnValidationSuccess(FormProcessData)} returns true.
     * 
     * <p>
     * 
     * From parent:
     * <p>
     * {@inheritDoc}
     * 
     */
    @Override
    default CommonFormResult processForm(FormProcessData processData, T form) {
        CommonFormResult result = processValidation(processData, form);
        if (result == CommonFormResult.SUCCESS && doUpdateOnValidationSuccess(processData)) {
            try {
                updateForm(form);
                return CommonFormResult.SUCCESS;
            } catch (Exception ex) {
                LoggerFactory.getLogger(this.getClass()).error("error occured on step:" + processData.getStepCurrent(), ex);
                return CommonFormResult.FAILED;
            }
        } else {
            return result;
        }
    }

    /**
     * Whether we should call {@link #updateForm(Object)} when the {@link #processValidation(Object)} returns SUCCESS. The default is true.
     * 
     * @param processData
     * @return
     */
    default boolean doUpdateOnValidationSuccess(FormProcessData processData) {
        return true;
    }

    /**
     * Sub classes must override this method to implement update logic.
     * 
     * @param form
     */
    public void updateForm(T form);
}
