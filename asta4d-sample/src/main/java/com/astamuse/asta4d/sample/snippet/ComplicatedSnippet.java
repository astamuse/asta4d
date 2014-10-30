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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Tag;

import com.astamuse.asta4d.data.annotation.ContextData;
import com.astamuse.asta4d.render.ChildReplacer;
import com.astamuse.asta4d.render.GoThroughRenderer;
import com.astamuse.asta4d.render.Renderer;
import com.astamuse.asta4d.web.WebApplicationContext;
import com.astamuse.asta4d.web.annotation.QueryParam;

public class ComplicatedSnippet {

    // @ShowCode:showRenderertypesStart
    public Renderer render() {
        // a renderer which does nothing
        Renderer render = Renderer.create();

        // replace all the children nodes of the corresponding node by selector
        render.add("ul#childreplacer", new ChildReplacer(createElement()));

        // remove a node
        render.add("ul#clearnode", Clear);

        // output the whole html string of current node to log console
        render.addDebugger("current element");

        return render;
    }

    private Element createElement() {
        Element ul = new Element(Tag.valueOf("ul"), "");
        ul.appendChild(new Element(Tag.valueOf("li"), "").appendText("This text is created by snippet.(1)"));
        ul.appendChild(new Element(Tag.valueOf("li"), "").appendText("This text is created by snippet.(2)"));
        ul.appendChild(new Element(Tag.valueOf("li"), "").appendText("This text is created by snippet.(3)"));
        return ul;
    }

    // @ShowCode:showRenderertypesEnd

    // @ShowCode:showPassvariablesStart
    public Renderer outer() {
        // prepare all necessary data for the inner snippet
        Renderer render = Renderer.create();
        render.add("div#inner", "name", "baz");
        render.add("div#inner", "age", "30");
        render.add("div#inner", "currenttime", new Date());

        List<String> list = new ArrayList<>();
        list.add("This text is passed by outer snippet.(1)");
        list.add("This text is passed by outer snippet.(2)");
        list.add("This text is passed by outer snippet.(3)");

        render.add("div#inner", "list", list);

        return render;
    }

    public Renderer inner(String name, int age, Date currenttime, List<String> list) {
        // receive the parameters by method declaration and then make use of them
        Renderer render = new GoThroughRenderer();
        render.add("p#name span", name);
        render.add("p#age span", age);
        render.add("p#currenttime span", DateFormatUtils.format(currenttime, "yyyy/MM/dd HH:mm:ss"));
        render.add("ul#list li", list);
        return render;
    }

    // @ShowCode:showPassvariablesEnd

    // @ShowCode:showContextdataStart
    public Renderer changeName(@ContextData(name = "var") String changedName) {
        return Renderer.create("dd", changedName);
    }

    public Renderer specificScope(@ContextData(scope = WebApplicationContext.SCOPE_QUERYPARAM) String var) {
        return Renderer.create("dd", var == null ? "" : var);
    }

    public Renderer convenientAnnotation(@QueryParam String var) {
        return Renderer.create("dd", var == null ? "" : var);
    }

    // @ShowCode:showContextdataEnd

    // @ShowCode:showLocalizeStart
    public Renderer setMsgParam() {
        Renderer render = Renderer.create();
        render.add("#peoplecount", "p0", 15);
        return render;
    }

    // @ShowCode:showLocalizeEnd

}
