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
package com.astamuse.asta4d.sample.snippet.form;

import static com.astamuse.asta4d.render.SpecialRenderer.Clear;

import java.util.Arrays;
import java.util.List;

import com.astamuse.asta4d.render.Renderer;
import com.astamuse.asta4d.sample.util.persondb.Person;
import com.astamuse.asta4d.sample.util.persondb.PersonDbManager;
import com.astamuse.asta4d.util.SelectorUtil;
import com.astamuse.asta4d.web.annotation.QueryParam;

public class ListSnippet {

    public Renderer render() {
        List<Person> personList = PersonDbManager.instance().findAll();
        return Renderer.create(".x-row", personList, (p) -> {
            Renderer renderer = Renderer.create();
            renderer.add(".x-check input", "value", p.getId());
            renderer.add(".x-name", p.getName());
            renderer.add(".x-age", p.getAge());
            renderer.add(".x-sex", p.getSex());
            renderer.add(".x-blood", p.getBloodType());
            renderer.add(".x-language span", Arrays.asList(p.getLanguage()));
            return renderer;
        });
    }

    public Renderer showTabs(@QueryParam String type) {
        Renderer renderer = Renderer.create();
        renderer.add("li", "-class", "active");

        String targetLi = SelectorUtil.id("li", type);
        renderer.add(targetLi, "+class", "active");
        renderer.add(targetLi + " a", "href", Clear);

        String targetBtn = SelectorUtil.id("div", type);
        renderer.add(targetBtn, "-class", "x-remove");
        renderer.add(".x-remove", Clear);

        return renderer;
    }
}
