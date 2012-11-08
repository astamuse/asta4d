package com.astamuse.asta4d.format;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.astamuse.asta4d.extnode.ExtNodeConstants;

public abstract class ParamOrderDependentFormatter implements PlaceholderFormatter {

    @Override
    public String format(String pattern, Map<String, Object> paramMap) {
        Object[] params = retrieveNumberedParamKeyList(paramMap);
        return format(pattern, params);
    }

    public abstract String format(String pattern, Object... params);

    private static Object[] retrieveNumberedParamKeyList(Map<String, Object> paramMap) {
        List<Object> numberedParamNameList = new ArrayList<>();
        for (int index = 0; paramMap.containsKey(ExtNodeConstants.MSG_NODE_ATTR_PARAM_PREFIX + index); index++) {
            String key = ExtNodeConstants.MSG_NODE_ATTR_PARAM_PREFIX + index;
            Object value = paramMap.get(key);
            numberedParamNameList.add(value);
        }
        return numberedParamNameList.toArray();
    }
}
