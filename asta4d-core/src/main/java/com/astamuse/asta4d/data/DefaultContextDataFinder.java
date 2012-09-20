package com.astamuse.asta4d.data;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.astamuse.asta4d.Context;
import com.astamuse.asta4d.data.builtin.String2Bool;
import com.astamuse.asta4d.data.builtin.String2Int;
import com.astamuse.asta4d.data.builtin.String2Long;

public class DefaultContextDataFinder implements ContextDataFinder {

    private List<DataConvertor<?, ?>> dataConvertorList = getDefaultDataConvertorList();

    private List<String> dataSearchScopeOrder = getDefaultScopeOrder();

    private final static List<DataConvertor<?, ?>> getDefaultDataConvertorList() {
        // TODO perhaps we can implement an mechanism to automatically retrieve
        // all possible convertors via annotation check on all loaded classes.
        // Anyway, it is not necessary.
        List<DataConvertor<?, ?>> defaultList = new ArrayList<>();
        defaultList.add(new String2Long());
        defaultList.add(new String2Int());
        defaultList.add(new String2Bool());
        return defaultList;
    }

    private final static List<String> getDefaultScopeOrder() {
        List<String> list = new ArrayList<>();
        list.add(Context.SCOPE_ATTR);
        list.add(Context.SCOPE_DEFAULT);
        list.add(Context.SCOPE_GLOBAL);
        return list;
    }

    public List<DataConvertor<?, ?>> getDataConvertorList() {
        return dataConvertorList;
    }

    public void setDataConvertorList(List<DataConvertor<?, ?>> dataConvertorList) {
        List<DataConvertor<?, ?>> list = new ArrayList<>(dataConvertorList);
        list.addAll(getDefaultDataConvertorList());
        this.dataConvertorList = list;
    }

    public List<String> getDataSearchScopeOrder() {
        return dataSearchScopeOrder;
    }

    public void setDataSearchScopeOrder(List<String> dataSearchScopeOrder) {
        this.dataSearchScopeOrder = dataSearchScopeOrder;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public Object findDataInContext(Context context, String scope, String name, Class<?> type) throws DataOperationException {
        Object data = null;
        if (StringUtils.isEmpty(scope)) {
            data = findDataByScopeOrder(context, 0, name);
        } else {
            data = context.getData(scope, name);
        }

        if (data == null) {
            return null;
        }

        if (type.isAssignableFrom(data.getClass())) {
            return data;
        }

        DataConvertor convertor = getConvertor(data.getClass(), type);
        if (convertor == null) {
            String msg = String.format("Could not find appropriate data convertor for from type {} to type {}", data.getClass().getName(),
                    type.getName());
            throw new DataOperationException(msg);
        } else {
            return convertor.convert(data);
        }

    }

    private Object findDataByScopeOrder(Context context, int scopeIndex, String name) {
        if (scopeIndex >= dataSearchScopeOrder.size()) {
            return null;
        } else {
            Object data = context.getData(dataSearchScopeOrder.get(scopeIndex), name);
            if (data == null) {
                data = findDataByScopeOrder(context, scopeIndex + 1, name);
            }
            return data;
        }
    }

    private DataConvertor<?, ?> getConvertor(Class<?> srcType, Class<?> targetType) {
        // TODO need a cache
        try {
            Class<?> convertorSrcType, convertorTargetType;
            Method method;
            for (DataConvertor<?, ?> convertor : dataConvertorList) {
                method = convertor.getClass().getMethod("convert");
                convertorSrcType = method.getParameterTypes()[0];
                convertorTargetType = method.getReturnType();
                if (convertorSrcType.isAssignableFrom(srcType) && targetType.isAssignableFrom(convertorTargetType)) {
                    return convertor;
                }
            }
        } catch (NoSuchMethodException | SecurityException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

}
