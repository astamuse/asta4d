package com.astamuse.asta4d.data;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.builder.ToStringBuilder;

import com.astamuse.asta4d.Configuration;
import com.astamuse.asta4d.data.builtin.String2Bool;
import com.astamuse.asta4d.data.builtin.String2Int;
import com.astamuse.asta4d.data.builtin.String2Long;

public class DefaultDataConvertorInvoker implements DataConvertorInvoker {

    private ConcurrentHashMap<DataConvertorKey, ArrayDataConvertor<?, ?>> dataConvertorCache = new ConcurrentHashMap<>();

    private List<ArrayDataConvertor<?, ?>> dataConvertorList = getDefaultDataConvertorList();

    private final static List<ArrayDataConvertor<?, ?>> getDefaultDataConvertorList() {
        List<ArrayDataConvertor<?, ?>> defaultList = new ArrayList<>();
        defaultList.add(new String2Long());
        defaultList.add(new String2Int());
        defaultList.add(new String2Bool());
        return defaultList;
    }

    public List<ArrayDataConvertor<?, ?>> getDataConvertorList() {
        return dataConvertorList;
    }

    public void setDataConvertorList(List<ArrayDataConvertor<?, ?>> dataConvertorList) {
        List<ArrayDataConvertor<?, ?>> list = new ArrayList<>(dataConvertorList);
        list.addAll(getDefaultDataConvertorList());
        this.dataConvertorList = list;
    }

    @Override
    public Object convert(Object data, Class<?> targetType) throws DataOperationException {
        if (data == null) {
            return null;
        }
        Object result;
        Class<?> srcType = new TypeInfo(data.getClass()).getType();
        if (targetType.isAssignableFrom(srcType)) {
            result = data;
        } else if (srcType.isArray() && targetType.isAssignableFrom(srcType.getComponentType())) {
            result = Array.get(data, 0);
        } else if (targetType.isArray() && targetType.getComponentType().isAssignableFrom(srcType)) {
            Object array = Array.newInstance(srcType, 1);
            Array.set(array, 0, data);
            result = array;
        } else {
            result = convertData(srcType, targetType, data);
        }
        return result;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private Object convertData(Class<?> srcType, Class<?> targetType, Object data) throws DataOperationException {
        try {
            if (Configuration.getConfiguration().isCacheEnable()) {
                Object convertedData = convertFromCachedConverter(srcType, targetType, data);
                if (convertedData != null) {
                    return convertedData;
                }
            }
            Class<?> convertorSrcType, convertorTargetType;
            for (ArrayDataConvertor dc : dataConvertorList) {
                Method method = findConvertMethod(dc);
                convertorSrcType = method.getParameterTypes()[0];
                convertorTargetType = method.getReturnType();
                if (convertorSrcType.isAssignableFrom(srcType) && targetType.isAssignableFrom(convertorTargetType)) {
                    dataConvertorCache.put(new DataConvertorKey(srcType, targetType, ConvertType.ELEMENT_TO_ELEMENT), dc);
                    return dc.convert(data);
                }

                if (srcType.isArray() && targetType.isArray() && convertorSrcType.isAssignableFrom(srcType.getComponentType()) &&
                        targetType.getComponentType().isAssignableFrom(convertorTargetType)) {
                    dataConvertorCache.put(new DataConvertorKey(srcType, targetType, ConvertType.ARRAY_TO_ARRAY), dc);
                    return dc.convertFromToArray((Object[]) data);
                }

                if (srcType.isArray() && convertorSrcType.isAssignableFrom(srcType.getComponentType()) &&
                        targetType.isAssignableFrom(convertorTargetType)) {
                    dataConvertorCache.put(new DataConvertorKey(srcType, targetType, ConvertType.ARRAY_TO_ELEMENT), dc);
                    return dc.convertFromArray((Object[]) data);
                }

                if (targetType.isArray() && convertorSrcType.isAssignableFrom(srcType) &&
                        targetType.getComponentType().isAssignableFrom(convertorTargetType)) {
                    dataConvertorCache.put(new DataConvertorKey(srcType, targetType, ConvertType.ELEMENT_TO_ARRAY), dc);
                    return dc.convertToArray(data);
                }
            }
        } catch (SecurityException e) {
            throw new RuntimeException(e);
        }
        String msg = String.format("Could not find appropriate data convertor for from type %s to type %s", data.getClass().getName(),
                targetType.getName());
        throw new DataOperationException(msg);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private Object convertFromCachedConverter(Class<?> srcType, Class<?> targetType, Object data) {
        ArrayDataConvertor cachedConvertor = dataConvertorCache.get(new DataConvertorKey(srcType, targetType,
                ConvertType.ELEMENT_TO_ELEMENT));
        if (cachedConvertor != null) {
            return cachedConvertor.convert(data);
        }
        cachedConvertor = dataConvertorCache.get(new DataConvertorKey(srcType, targetType, ConvertType.ARRAY_TO_ARRAY));
        if (cachedConvertor != null) {
            return cachedConvertor.convertFromToArray((Object[]) data);
        }
        cachedConvertor = dataConvertorCache.get(new DataConvertorKey(srcType, targetType, ConvertType.ARRAY_TO_ELEMENT));
        if (cachedConvertor != null) {
            return cachedConvertor.convertFromArray((Object[]) data);
        }
        cachedConvertor = dataConvertorCache.get(new DataConvertorKey(srcType, targetType, ConvertType.ELEMENT_TO_ARRAY));
        if (cachedConvertor != null) {
            return cachedConvertor.convertToArray(data);
        }
        return null;
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

    private enum ConvertType {
        ELEMENT_TO_ELEMENT, ELEMENT_TO_ARRAY, ARRAY_TO_ELEMENT, ARRAY_TO_ARRAY;
    }

    private static final class DataConvertorKey {
        private final String srcType;
        private final String targetType;
        private final ConvertType convertType;

        public DataConvertorKey(Class<?> srcType, Class<?> targetType, ConvertType convertType) {
            if (srcType.isArray()) {
                this.srcType = srcType.getComponentType().getName() + "[]";
            } else {
                this.srcType = srcType.getName();
            }
            if (targetType.isArray()) {
                this.targetType = targetType.getComponentType().getName() + "[]";
            } else {
                this.targetType = targetType.getName();
            }
            this.convertType = convertType;
        }

        @Override
        public String toString() {
            return ToStringBuilder.reflectionToString(this);
        }

        @Override
        public int hashCode() {
            // HashCodeBuilder.reflectionHashCode is not use for performance
            final int prime = 31;
            int result = 1;
            result = prime * result + ((convertType == null) ? 0 : convertType.hashCode());
            result = prime * result + ((srcType == null) ? 0 : srcType.hashCode());
            result = prime * result + ((targetType == null) ? 0 : targetType.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            // EqualsBuilder.reflectionEquals is not use for performance
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (!(obj instanceof DataConvertorKey)) {
                return false;
            }
            DataConvertorKey other = (DataConvertorKey) obj;
            if (this.convertType != other.convertType) {
                return false;
            }
            if (this.srcType == null) {
                if (other.srcType != null) {
                    return false;
                }
            } else if (!this.srcType.equals(other.srcType)) {
                return false;
            }
            if (this.targetType == null) {
                if (other.targetType != null) {
                    return false;
                }
            } else if (!this.targetType.equals(other.targetType)) {
                return false;
            }
            return true;
        }
    }
}
