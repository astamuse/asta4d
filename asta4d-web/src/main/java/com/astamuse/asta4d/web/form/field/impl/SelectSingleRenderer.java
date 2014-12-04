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
package com.astamuse.asta4d.web.form.field.impl;

import com.astamuse.asta4d.render.Renderer;

public class SelectSingleRenderer extends AbstractSelectRenderer {

    @Override
    public Renderer renderForEdit(String editTargetSelector, Object value) {
        if (value == null) {
            // for a null value, we need to cheat it as an array with one null element
            return super.renderForEdit(editTargetSelector, new Object[] { getNonNullString(null) });
        } else {
            return super.renderForEdit(editTargetSelector, value);
        }
    }

    @Override
    public Renderer renderForDisplay(final String editTargetSelector, final String displayTargetSelector, final Object value) {
        if (value == null) {
            // for a null value, we need to cheat it as an array with one null element
            return super.renderForDisplay(editTargetSelector, displayTargetSelector, new Object[] { getNonNullString(null) });
        } else {
            return super.renderForDisplay(editTargetSelector, displayTargetSelector, value);
        }
    }

}
