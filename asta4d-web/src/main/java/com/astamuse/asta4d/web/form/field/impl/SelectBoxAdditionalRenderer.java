package com.astamuse.asta4d.web.form.field.impl;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.tuple.Pair;

import com.astamuse.asta4d.render.Renderer;
import com.astamuse.asta4d.util.collection.RowRenderer;
import com.astamuse.asta4d.web.form.field.SimpleFormFieldAdditionalRenderer;

public class SelectBoxAdditionalRenderer extends SimpleFormFieldAdditionalRenderer {

    private static class OptGroup {

        String groupName;
        List<Pair<String, String>> optionList;

        public OptGroup(String groupName, List<Pair<String, String>> optionList) {
            super();
            this.groupName = groupName;
            this.optionList = optionList;
        }
    }

    private List<OptGroup> optGroupList = new LinkedList<>();

    private List<Pair<String, String>> optionList = null;

    public SelectBoxAdditionalRenderer(Field field) {
        super(field);
    }

    public SelectBoxAdditionalRenderer(Class cls, String fieldName) {
        super(cls, fieldName);
    }

    public SelectBoxAdditionalRenderer setOptionData(List<Pair<String, String>> optionList) {
        if (CollectionUtils.isNotEmpty(optGroupList)) {
            throw new RuntimeException("Option list without group is not allowed because there are existing option groups");
        }
        this.optionList = optionList;
        return this;
    }

    public SelectBoxAdditionalRenderer addOptionGroup(String groupName, List<Pair<String, String>> optionList) {
        if (CollectionUtils.isNotEmpty(optionList)) {
            throw new RuntimeException("Option list group is not allowed because there are existing option list without group");
        }
        optGroupList.add(new OptGroup(groupName, optionList));
        return this;
    }

    @Override
    public Renderer preRender(String editSelector, String displaySelector) {

        Map<String, String> storingValueMap = new HashMap<>();

        Renderer renderer = super.preRender(editSelector, displaySelector);
        if (CollectionUtils.isNotEmpty(optGroupList)) {
            renderer.add(renderOptionGroup(editSelector, optGroupList, storingValueMap));
        } else if (CollectionUtils.isNotEmpty(optionList)) {
            renderer.add(renderOptionList(editSelector, optionList, storingValueMap));
        }

        SelectBoxRenderer.storeValueMapToContext(editSelector, displaySelector, storingValueMap);

        return renderer;
    }

    private Renderer renderOptionGroup(String editSelector, List<OptGroup> groupList, final Map<String, String> valueMap) {
        return Renderer.create(editSelector, Renderer.create("optGroup:eq(0)", groupList, new RowRenderer<OptGroup>() {
            @Override
            public Renderer convert(int rowIndex, OptGroup row) {
                return Renderer.create("optGroup", "label", row.groupName).add(renderOptionList("optGroup", row.optionList, valueMap));
            }
        }));
    }

    private Renderer renderOptionList(String editSelector, List<Pair<String, String>> optList, final Map<String, String> valueMap) {

        return Renderer.create(editSelector, Renderer.create("option:eq(0)", optList, new RowRenderer<Pair<String, String>>() {
            @Override
            public Renderer convert(int rowIndex, Pair<String, String> row) {
                valueMap.put(row.getLeft(), row.getRight());
                return Renderer.create("option", "value", row.getLeft()).add("option", row.getRight());
            }
        }));
    }

}
