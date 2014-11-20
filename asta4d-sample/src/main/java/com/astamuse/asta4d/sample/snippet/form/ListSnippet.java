package com.astamuse.asta4d.sample.snippet.form;

import static com.astamuse.asta4d.render.SpecialRenderer.Clear;

import java.util.Arrays;
import java.util.List;

import com.astamuse.asta4d.render.Renderer;
import com.astamuse.asta4d.sample.util.persondb.Person;
import com.astamuse.asta4d.sample.util.persondb.PersonDbManager;
import com.astamuse.asta4d.util.SelectorUtil;
import com.astamuse.asta4d.util.collection.RowRenderer;
import com.astamuse.asta4d.web.annotation.QueryParam;

public class ListSnippet {

    public Renderer render() {
        List<Person> personList = PersonDbManager.instance().findAll();
        return Renderer.create(".x-row", personList, new RowRenderer<Person>() {
            @Override
            public Renderer convert(int rowIndex, Person row) {
                Renderer renderer = Renderer.create();
                renderer.add(".x-check input", "value", row.getId());
                renderer.add(".x-name", row.getName());
                renderer.add(".x-age", row.getAge());
                renderer.add(".x-sex", row.getSex());
                renderer.add(".x-blood", row.getBloodType());
                renderer.add(".x-language span", Arrays.asList(row.getLanguage()));
                return renderer;
            }
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
