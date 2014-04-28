/*
 * Copyright 2012 astamuse company,Ltd.
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

package com.astamuse.asta4d.sample.snippet;

import java.util.Arrays;

import com.astamuse.asta4d.render.Renderer;
import com.astamuse.asta4d.sample.newform.MyForm;
import com.astamuse.asta4d.sample.newform.MyForm.BloodType;
import com.astamuse.asta4d.util.collection.RowRenderer;

public class NewFormSnippet extends CommonFormSnippet<MyForm> {

    @Override
    public Renderer render() throws Exception {
        Renderer render = super.render();
        return render.add(renderSupportData(form));
    }

    private Renderer renderSupportData(MyForm form) {
        return Renderer.create("#bloodtype option", Arrays.asList(MyForm.BloodType.values()), new RowRenderer<MyForm.BloodType>() {
            @Override
            public Renderer convert(int rowIndex, BloodType row) {
                Renderer render = Renderer.create("option", "value", row.name());
                render.add("option", row.name());
                return render;
            }
        });
    }

}
