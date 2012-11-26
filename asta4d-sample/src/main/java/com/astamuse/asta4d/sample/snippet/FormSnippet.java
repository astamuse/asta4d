package com.astamuse.asta4d.sample.snippet;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.astamuse.asta4d.render.GoThroughRenderer;
import com.astamuse.asta4d.render.Renderer;

public class FormSnippet {
    private enum BloodType {
        A, B, O, AB;
    }

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

    public Renderer setInputValue(String name, String age, String bloodtype) {
        Renderer renderer = new GoThroughRenderer();
        renderer.add("dd.name", name);
        renderer.add("dd.age", age);
        renderer.add("dd.bloodtype", BloodType.valueOf(bloodtype).name());
        return renderer;
    }

    public Renderer setHiddenValue(String name, String age, String bloodtype) {
        Renderer renderer = new GoThroughRenderer();
        renderer.add("input#name", "value", name);
        renderer.add("input#age", "value", age);
        renderer.add("input#bloodtype", "value", bloodtype);
        return renderer;
    }
}
