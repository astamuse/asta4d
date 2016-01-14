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

import com.astamuse.asta4d.render.Renderer;
import com.astamuse.asta4d.web.form.flow.base.BasicFormFlowSnippetTrait;
import com.astamuse.asta4d.web.form.flow.base.FormFlowTraceData;

/**
 * 
 * This trait represents the rendering way of a classical one step form flow which contains only one input step.
 * 
 * @author e-ryu
 *
 */
public interface OneStepFormSnippetTrait extends BasicFormFlowSnippetTrait {

    default boolean renderForEdit(String step, Object form, String fieldName) {
        return true;
    }

    default Renderer renderTraceData(FormFlowTraceData traceData) {
        return Renderer.create();
    }

}
