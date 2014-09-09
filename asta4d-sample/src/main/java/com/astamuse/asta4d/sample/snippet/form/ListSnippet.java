package com.astamuse.asta4d.sample.snippet.form;

import java.util.Arrays;
import java.util.List;

import com.astamuse.asta4d.render.Renderer;
import com.astamuse.asta4d.sample.util.persondb.DbManager;
import com.astamuse.asta4d.sample.util.persondb.Person;
import com.astamuse.asta4d.util.collection.RowRenderer;

public class ListSnippet {

    public Renderer render() {
        List<Person> personList = DbManager.instance().getAll();
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
}
