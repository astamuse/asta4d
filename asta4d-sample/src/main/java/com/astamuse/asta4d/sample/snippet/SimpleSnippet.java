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

import static com.astamuse.asta4d.render.SpecialRenderer.Clear;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Element;

import com.astamuse.asta4d.render.GoThroughRenderer;
import com.astamuse.asta4d.render.Renderer;
import com.astamuse.asta4d.util.ElementUtil;

public class SimpleSnippet {

    // @ShowCode:showSnippetStart
    // @ShowCode:showVariableinjectionStart
    public Renderer render(String name) {
        // replace the whole title node
        if (StringUtils.isEmpty(name)) {
            name = "Asta4D";
        }
        Element element = ElementUtil.parseAsSingle("<div>Hello " + name + "!</div>");
        return Renderer.create(":root", element);
    }

    public Renderer setProfileByVariableInjection(String name, int age) {
        Renderer render = Renderer.create();
        render.add("p#name span", name);
        render.add("p#age span", age);
        return render;
    }

    // @ShowCode:showVariableinjectionEnd

    public Renderer setProfile() {
        Renderer render = Renderer.create();
        render.add("p#name span", "asta4d");
        render.add("p#age span", 20);
        return render;
    }

    // @ShowCode:showSnippetEnd

    // @ShowCode:showAttributevaluesStart
    public Renderer manipulateAttrValues() {
        Renderer render = new GoThroughRenderer();
        render.add("input#yes", "checked", "checked");
        render.add("button#delete", "disabled", Clear);
        render.add("li#plus", "+class", "red");
        render.add("li#minus", "-class", "bold");
        return render;
    }

    // @ShowCode:showAttributevaluesEnd

    public Renderer errorOccurs() {
        throw new RuntimeException("Error occurs at snippet side");
    }
}
