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

import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.astamuse.asta4d.web.WebApplicationConfiguration;
import com.astamuse.asta4d.web.form.flow.classical.ClassicalMultiStepFormFlowHandlerTrait;
import com.astamuse.asta4d.web.util.SecureIdGenerator;

/**
 * This interface holds the functionalities about trace data persisting.
 * 
 * @author e-ryu
 *
 */
public interface FormFlowTraceDataAccessor {

    default FormFlowTraceData createEmptyTraceData() {
        return new FormFlowTraceData();
    }

    /**
     * <b>Note</b>: In fact, we should not save the trace map when some steps such as init step to avoid unnecessary memory usage, thus we
     * call the {@link #skipSaveTraceMap(String, String, Map)} to decide save or not.
     * <p>
     * In other words ,the sub class have the responsibility to tell us save or not by overriding the method
     * {@link #skipSaveTraceData(String, String, FormFlowTraceData)}.
     * 
     * @param currentStep
     * @param renderTargetStep
     * @param traceData
     * @return
     */
    default String storeTraceData(String currentStep, String renderTargetStep, String traceId, FormFlowTraceData traceData) {
        String storeId = StringUtils.isEmpty(traceId) ? SecureIdGenerator.createEncryptedURLSafeId() : traceId;
        WebApplicationConfiguration.getWebApplicationConfiguration().getExpirableDataManager().put(storeId, traceData,
                traceDataExpireTimeInMilliSeconds());
        return storeId;
    }

    /**
     * Since we are lacking of necessary step information to judge if we should save or not, we only do the basic judgment for the init
     * step. The sub class have the responsibility to handle other cases.
     * 
     * @see ClassicalMultiStepFormFlowHandlerTrait#skipSaveTraceData(String, String, FormFlowTraceData)
     * 
     */
    default boolean skipStoreTraceData(String currentStep, String renderTargetStep, FormFlowTraceData traceData) {
        if (FormFlowConstants.FORM_STEP_BEFORE_FIRST.equals(currentStep)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 
     * retrieve the stored trace data.
     * 
     * @param traceId
     * @return
     * @see #saveTraceMap(String, String, Map)
     */
    default FormFlowTraceData retrieveTraceData(String traceId) {
        return WebApplicationConfiguration.getWebApplicationConfiguration().getExpirableDataManager().get(traceId,
                removeTraceDataWhenRetrieving());
    }

    /**
     * 
     * 
     * @return
     */
    default boolean removeTraceDataWhenRetrieving() {
        return false;
    }

    /**
     * 
     * clear the stored trace map.
     * 
     * @param traceData
     * @see #saveTraceMap(String, String, Map)
     */
    default void clearStoredTraceData(String traceId) {
        if (StringUtils.isNotEmpty(traceId)) {
            WebApplicationConfiguration.getWebApplicationConfiguration().getExpirableDataManager().get(traceId, true);
        }
    }

    /**
     * Sub classes can override this method to customize how long the form flow trace data will keep alive.
     * <p>
     * The default value is 30 minutes.
     * 
     * @return
     */
    default long traceDataExpireTimeInMilliSeconds() {
        // 30 minutes
        return 30 * 60 * 1000L;
    }
}
