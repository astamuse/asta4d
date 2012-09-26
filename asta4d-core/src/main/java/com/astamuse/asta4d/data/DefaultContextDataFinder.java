package com.astamuse.asta4d.data;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.StringUtils;

import com.astamuse.asta4d.Context;
import com.astamuse.asta4d.data.builtin.String2Bool;
import com.astamuse.asta4d.data.builtin.String2Int;
import com.astamuse.asta4d.data.builtin.String2Long;

/**
 * A default implementation of {@link ContextDataFinder}. It will search data in
 * a given order and try to apply predefined {@link ContextDataFinder} list to
 * convert data to appropriate type.
 * 
 * @author e-ryu
 * 
 */
public class DefaultContextDataFinder implements ContextDataFinder {

    private ConcurrentHashMap<String, DataConvertor<?, ?>> dataConvertorCache = new ConcurrentHashMap<>();

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
        try {
            String cachekey = targetType.getName() + "<=" + srcType.getName();
            DataConvertor<?, ?> convertor = null;
            if (Context.getCurrentThreadContext().getConfiguration().isCacheEnable()) {
                convertor = dataConvertorCache.get(cachekey);
            }
            if (convertor == null) {
                Class<?> convertorSrcType, convertorTargetType;
                Method method;
                for (DataConvertor<?, ?> dc : dataConvertorList) {
                    method = findConvertMethod(dc);
                    convertorSrcType = method.getParameterTypes()[0];
                    convertorTargetType = method.getReturnType();
                    if (convertorSrcType.isAssignableFrom(srcType) && targetType.isAssignableFrom(convertorTargetType)) {
                        convertor = dc;
                        dataConvertorCache.put(cachekey, dc);
                        break;
                    }
                }
            }
            return convertor;
        } catch (SecurityException e) {
            throw new RuntimeException(e);
        }
    }

    private Method findConvertMethod(DataConvertor<?, ?> convertor) {
        Method[] methods = convertor.getClass().getMethods();
        Method rtnMethod = null;
        for (Method m : methods) {
            if (m.getName().equals("convert") && !m.isBridge()) {
                rtnMethod = m;
                break;
            }
        }
        return rtnMethod;
    }

}
