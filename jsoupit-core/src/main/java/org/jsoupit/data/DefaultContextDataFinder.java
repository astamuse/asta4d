package org.jsoupit.data;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.jsoupit.Context;
import org.jsoupit.data.adapter.String2Int;

public class DefaultContextDataFinder implements ContextDataFinder {

    private List<ContextDataAdapter> dataAapterList = getDefaultDataAdapterList();

    private List<String> dataSearchScopeOrder = getDefaultScopeOrder();

    private final static List<ContextDataAdapter> getDefaultDataAdapterList() {
        // TODO perhaps we can implement an mechanism to automatically retrieve
        // all possible adapters via annotation check on all loaded classes.
        // Anyway, it is not necessary.
        List<ContextDataAdapter> defaultList = new ArrayList<>();
        defaultList.add(new String2Int());
        return defaultList;
    }

    private final static List<String> getDefaultScopeOrder() {
        List<String> list = new ArrayList<>();
        list.add(Context.SCOPE_ATTR);
        list.add(Context.SCOPE_DEFAULT);
        list.add(Context.SCOPE_GLOBAL);
        return list;
    }

    public List<ContextDataAdapter> getDataAdapterList() {
        return dataAapterList;
    }

    public void setDataAapterList(List<ContextDataAdapter> dataAapterList) {
        List<ContextDataAdapter> list = new ArrayList<>(dataAapterList);
        list.addAll(getDefaultDataAdapterList());
        this.dataAapterList = list;
    }

    public List<String> getDataSearchScopeOrder() {
        return dataSearchScopeOrder;
    }

    public void setDataSearchScopeOrder(List<String> dataSearchScopeOrder) {
        this.dataSearchScopeOrder = dataSearchScopeOrder;
    }

    @Override
    public Object findDataInContext(Context context, String scope, String name, Class<?> type) {
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

        ContextDataAdapter adapter = getAdapter(data.getClass(), type);
        if (adapter == null) {
            // TODO what to do?
            return null;
        } else {
            return adapter.convert(data);
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

    private ContextDataAdapter getAdapter(Class<?> srcType, Class<?> targetType) {
        // TODO need a cache
        for (ContextDataAdapter adapter : dataAapterList) {
            if (adapter.getSourceType().isAssignableFrom(srcType) && targetType.isAssignableFrom(adapter.getTargetType())) {
                return adapter;
            }
        }
        return null;
    }

}
