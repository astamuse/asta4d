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

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.astamuse.asta4d.render.GoThroughRenderer;
import com.astamuse.asta4d.render.Renderer;

public class FormSnippet {
    // @ShowCode:showInputStart
    // @ShowCode:showConfirmStart
    // @ShowCode:showCompleteStart
    private enum BloodType {
        A, B, O, AB;
    }

    // @ShowCode:showConfirmEnd
    // @ShowCode:showCompleteEnd
    // @ShowCode:showInputEnd

    // @ShowCode:showInputStart
    public Renderer setInitValue(String name, String age, String bloodtype, String nameErrMsg, String ageErrMsg) {
        Renderer renderer = new GoThroughRenderer();
        if (!StringUtils.isEmpty(name)) {
            renderer.add("input#name", "value", name);
        }
        if (!StringUtils.isEmpty(age)) {
            renderer.add("input#age", "value", age);
        }
        if (!StringUtils.isEmpty(nameErrMsg)) {
            renderer.add("span#nameErrMsg", nameErrMsg);
        }
        if (!StringUtils.isEmpty(ageErrMsg)) {
            renderer.add("span#ageErrMsg", ageErrMsg);
        }
        List<Renderer> options = new ArrayList<>();
        for (BloodType bloodTypeEnum : BloodType.values()) {
            Renderer type = Renderer.create("option", "value", bloodTypeEnum.name());
            type.add("option", bloodTypeEnum.name());
            if (bloodTypeEnum.name().equals(bloodtype)) {
                type.add("option", "selected", "selected");
            }
            options.add(type);
        }
        renderer.add("select#bloodtype > option", options);
        return renderer;
    }

    // @ShowCode:showInputEnd

    // @ShowCode:showConfirmStart
    // @ShowCode:showCompleteStart
    public Renderer setInputValue(String name, String age, String bloodtype) {
        Renderer renderer = new GoThroughRenderer();
        renderer.add("dd.name", name);
        renderer.add("dd.age", age);
        renderer.add("dd.bloodtype", BloodType.valueOf(bloodtype).name());
        return renderer;
    }

    // @ShowCode:showCompleteEnd

    public Renderer setHiddenValue(String name, String age, String bloodtype) {
        Renderer renderer = new GoThroughRenderer();
        renderer.add("input#name", "value", name);
        renderer.add("input#age", "value", age);
        renderer.add("input#bloodtype", "value", bloodtype);
        return renderer;
    }
    // @ShowCode:showConfirmEnd
}
