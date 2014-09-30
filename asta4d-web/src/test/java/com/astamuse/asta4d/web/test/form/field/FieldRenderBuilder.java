package com.astamuse.asta4d.web.test.form.field;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

import com.astamuse.asta4d.render.Renderer;
import com.astamuse.asta4d.util.SelectorUtil;
import com.astamuse.asta4d.web.form.field.FormFieldDataPrepareRenderer;
import com.astamuse.asta4d.web.form.field.FormFieldValueRenderer;
import com.astamuse.asta4d.web.form.field.SimpleFormFieldAdditionalRenderer;

public class FieldRenderBuilder {

    Class<? extends FormFieldValueRenderer> valueRenderCls;

    private List<Pair<String, Object>> valueList = new LinkedList<>();

    private List<FormFieldDataPrepareRenderer> prepareList = new LinkedList<>();

    private FieldRenderBuilder() {

    }

    public static FieldRenderBuilder of(Class<? extends FormFieldValueRenderer> valueRenderCls) {
        FieldRenderBuilder builder = new FieldRenderBuilder();
        builder.valueRenderCls = valueRenderCls;
        return builder;
    }

    public FieldRenderBuilder addPrepare(FormFieldDataPrepareRenderer prepare) {
        prepareList.add(prepare);
        return this;
    }

    public FieldRenderBuilder addValue(String fieldName, Object value) {
        valueList.add(Pair.of(fieldName, value));
        return this;
    }

    private String editSelector(String fieldName) {
        return SelectorUtil.attr("name", fieldName);
    }

    private String displaySelector(String fieldName) {
        return SelectorUtil.id(fieldName + "-display");
    }

    public Renderer toRenderer(boolean forEdit) {
        Renderer renderer = Renderer.create();

        for (FormFieldDataPrepareRenderer prepare : prepareList) {
            String fieldName = ((SimpleFormFieldAdditionalRenderer) prepare).getGivenFieldName();
            renderer.add(prepare.preRender(editSelector(fieldName), displaySelector(fieldName)));
        }

        FormFieldValueRenderer valueRenderer;
        try {
            valueRenderer = valueRenderCls.newInstance();
            for (Pair<String, Object> value : valueList) {
                String edit = editSelector(value.getKey());
                String display = displaySelector(value.getKey());
                renderer.add(forEdit ? valueRenderer.renderForEdit(edit, value.getValue()) : valueRenderer.renderForDisplay(edit, display,
                        value.getValue()));
            }
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        for (FormFieldDataPrepareRenderer prepare : prepareList) {
            String fieldName = ((SimpleFormFieldAdditionalRenderer) prepare).getGivenFieldName();
            renderer.add(prepare.postRender(editSelector(fieldName), displaySelector(fieldName)));
        }

        return renderer;
    }
}
