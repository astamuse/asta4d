package com.astamuse.asta4d.web.form.field.impl;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import com.astamuse.asta4d.render.Renderer;
import com.astamuse.asta4d.util.annotation.AnnotatedPropertyInfo;
import com.astamuse.asta4d.util.collection.RowRenderer;
import com.astamuse.asta4d.web.form.field.PrepareRenderingDataUtil;
import com.astamuse.asta4d.web.form.field.OptionValueMap;
import com.astamuse.asta4d.web.form.field.OptionValuePair;
import com.astamuse.asta4d.web.form.field.SimpleFormFieldPrepareRenderer;

public class SelectPrepareRenderer extends SimpleFormFieldPrepareRenderer {

    private static class OptGroup {

        String groupName;
        OptionValueMap optionMap;

        public OptGroup(String groupName, OptionValueMap optionMap) {
            super();
            this.groupName = groupName;
            this.optionMap = optionMap;
        }
    }

    private List<OptGroup> optGroupList = new LinkedList<>();

    private OptionValueMap optionMap;

    public SelectPrepareRenderer(AnnotatedPropertyInfo field) {
        super(field);
    }

    public SelectPrepareRenderer(Class cls, String fieldName) {
        super(cls, fieldName);
    }

    /**
     * for test purpose, DO NOT USE IT!!!
     * 
     * @param fieldName
     */
    @Deprecated
    public SelectPrepareRenderer(String fieldName) {
        super(fieldName);
    }

    public SelectPrepareRenderer setOptionData(OptionValueMap optionMap) {
        if (CollectionUtils.isNotEmpty(optGroupList)) {
            throw new RuntimeException("Option list without group is not allowed because there are existing option groups");
        }
        this.optionMap = optionMap;
        return this;
    }

    public SelectPrepareRenderer addOptionGroup(String groupName, OptionValueMap optionMap) {
        if (this.optionMap != null) {
            throw new RuntimeException("Option list group is not allowed because there are existing option list without group");
        }
        this.optGroupList.add(new OptGroup(groupName, optionMap));
        return this;
    }

    @Override
    public Renderer preRender(String editSelector, String displaySelector) {
        Renderer renderer = super.preRender(editSelector, displaySelector);
        if (CollectionUtils.isNotEmpty(optGroupList)) {
            renderer.add(renderOptionGroup(editSelector, optGroupList));
            List<OptionValuePair> allList = new LinkedList<>();
            for (OptGroup optGrp : optGroupList) {
                allList.addAll(optGrp.optionMap.getOptionList());
            }
            OptionValueMap allMap = new OptionValueMap(allList);
            PrepareRenderingDataUtil.storeDataToContextBySelector(editSelector, displaySelector, allMap);
        } else if (optionMap != null) {
            renderer.add(renderOptionList(editSelector, optionMap));
            PrepareRenderingDataUtil.storeDataToContextBySelector(editSelector, displaySelector, optionMap);
        }

        return renderer;
    }

    private Renderer renderOptionGroup(String editSelector, List<OptGroup> groupList) {
        return Renderer.create(editSelector, Renderer.create("optGroup:eq(0)", groupList, new RowRenderer<OptGroup>() {
            @Override
            public Renderer convert(int rowIndex, OptGroup row) {
                return Renderer.create("optGroup", "label", row.groupName).add(renderOptionList("optGroup", row.optionMap));
            }
        }));
    }

    private Renderer renderOptionList(String editSelector, final OptionValueMap valueMap) {

        return Renderer.create(editSelector, Renderer.create("option:eq(0)", valueMap.getOptionList(), new RowRenderer<OptionValuePair>() {
            @Override
            public Renderer convert(int rowIndex, OptionValuePair row) {
                return Renderer.create("option", "value", row.getValue()).add("option", row.getDisplayText());
            }
        }));
    }

}
