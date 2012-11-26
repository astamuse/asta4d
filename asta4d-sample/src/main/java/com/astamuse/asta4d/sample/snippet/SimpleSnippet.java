package com.astamuse.asta4d.sample.snippet;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Element;

import com.astamuse.asta4d.render.GoThroughRenderer;
import com.astamuse.asta4d.render.Renderer;
import com.astamuse.asta4d.util.ElementUtil;

public class SimpleSnippet {

    public Renderer render(String name) {
        if (StringUtils.isEmpty(name)) {
            name = "Asta4D";
        }
        Element element = ElementUtil.parseAsSingle("<span>Hello " + name + "!</span>");
        return Renderer.create("*", element);
    }

    public Renderer setProfile() {
        Renderer render = new GoThroughRenderer();
        render.add("p#name span", "asta4d");
        render.add("p#age span", "20");
        return render;
    }

    public Renderer setProfileByVariableInjection(String name, int age) {
        Renderer render = new GoThroughRenderer();
        render.add("p#name span", name);
        render.add("p#age span", age);
        return render;
    }

    public Renderer manipulateAttrValues() {
        Renderer render = new GoThroughRenderer();
        render.add("input#yes", "checked", "checked");
        render.add("button#delete", "disabled", null);
        render.add("li#plus", "+class", "red");
        render.add("li#minus", "-class", "bold");
        return render;
    }
}
