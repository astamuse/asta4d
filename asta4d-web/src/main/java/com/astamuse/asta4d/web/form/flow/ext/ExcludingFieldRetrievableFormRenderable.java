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
package com.astamuse.asta4d.web.form.flow.ext;

import static com.astamuse.asta4d.render.SpecialRenderer.Clear;

import com.astamuse.asta4d.render.Renderer;
import com.astamuse.asta4d.web.form.flow.base.BasicFormFlowSnippetTrait;

public interface ExcludingFieldRetrievableFormRenderable extends BasicFormFlowSnippetTrait {

    @Override
    default Renderer preRenderForm(String renderTargetStep, Object form, int[] indexes) {
        return formFieldExcludeRendering(renderTargetStep, form, indexes);
    }

    default Renderer formFieldExcludeRendering(String renderTargetStep, Object form, int[] indexes) {
        Renderer renderer = Renderer.create();
        if (form instanceof ExcludingFieldRetrievableForm) {
            ExcludingFieldRetrievableForm desc = (ExcludingFieldRetrievableForm) form;
            String[] fields = desc.getExcludeFields();
            for (String field : fields) {
                renderer.add(exludeField(field));
            }
        }
        return renderer;
    }

    default Renderer exludeField(String field) {
        Renderer renderer = Renderer.create();
        for (String s : clearExcludeFieldsSelectors(field)) {
            if (clearExcludeFields()) {
                renderer.add(s, Clear);
            } else {
                renderer.add(s, "display", "none");
            }
        }
        return renderer;
    }

    default String[] clearExcludeFieldsSelectors(String field) {
        return new String[] { "[name=" + field + "]", "#" + field + "-container" };
    }

    default boolean clearExcludeFields() {
        return true;
    }

}
