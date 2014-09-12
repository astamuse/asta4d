package com.astamuse.asta4d.web.form.field;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.astamuse.asta4d.util.collection.ListConvertUtil;
import com.astamuse.asta4d.util.collection.RowConvertor;

public class OptionValueMap implements Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private List<OptionValuePair> optionList;

    private Map<String, String> valueMap;

    public OptionValueMap(List<OptionValuePair> optionList) {
        this.optionList = optionList;
        valueMap = new HashMap<>();
        for (OptionValuePair op : optionList) {
            valueMap.put(op.getValue(), op.getDisplayText());
        }
    }

    public static final <S> OptionValueMap build(List<S> list, RowConvertor<S, OptionValuePair> convertor) {
        return new OptionValueMap(ListConvertUtil.transform(list, convertor));
    }

    public static final <S> OptionValueMap build(S[] array, RowConvertor<S, OptionValuePair> convertor) {
        return new OptionValueMap(ListConvertUtil.transform(Arrays.asList(array), convertor));
    }

    public List<OptionValuePair> getOptionList() {
        return Collections.unmodifiableList(optionList);
    }

    public String getDisplayText(String value) {
        return valueMap.get(value);
    }
}
