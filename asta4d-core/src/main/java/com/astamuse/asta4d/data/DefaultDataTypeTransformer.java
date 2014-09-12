package com.astamuse.asta4d.data;

import java.lang.reflect.Array;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.astamuse.asta4d.Configuration;
import com.astamuse.asta4d.data.convertor.DataValueConvertor;
import com.astamuse.asta4d.data.convertor.DataValueConvertorTargetTypeConvertable;
import com.astamuse.asta4d.data.convertor.String2Bool;
import com.astamuse.asta4d.data.convertor.String2Enum;
import com.astamuse.asta4d.data.convertor.String2Int;
import com.astamuse.asta4d.data.convertor.String2Long;
import com.astamuse.asta4d.data.convertor.UnsupportedValueException;
import com.astamuse.asta4d.util.collection.ListConvertUtil;
import com.astamuse.asta4d.util.collection.RowConvertor;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class DefaultDataTypeTransformer implements DataTypeTransformer {

    private static final Logger logger = LoggerFactory.getLogger(DefaultDataTypeTransformer.class);

    private static final class DataTypeConvertorKey {
        private String srcTypeName;
        private String targetTypeName;
        private int hashCode;

        DataTypeConvertorKey(Class srcType, Class targetType) {
            this.srcTypeName = srcType.getName();
            this.targetTypeName = targetType.getName();
            this.hashCode = this.srcTypeName.hashCode() ^ this.targetTypeName.hashCode();
        }

        @Override
        public int hashCode() {
            return hashCode;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            DataTypeConvertorKey other = (DataTypeConvertorKey) obj;
            if (srcTypeName == null) {
                if (other.srcTypeName != null)
                    return false;
            } else if (!srcTypeName.equals(other.srcTypeName))
                return false;
            if (targetTypeName == null) {
                if (other.targetTypeName != null)
                    return false;
            } else if (!targetTypeName.equals(other.targetTypeName))
                return false;
            return true;
        }

    }

    private Map<DataTypeConvertorKey, List<DataValueConvertor>> convertorCacheMap = new ConcurrentHashMap<>();

    private List<DataValueConvertor> DataTypeConvertorList = getDefaultDataTypeConvertorList();

    private final static List<DataValueConvertor> getDefaultDataTypeConvertorList() {
        List<DataValueConvertor> defaultList = new ArrayList<>();
        defaultList.add(new String2Long());
        defaultList.add(new String2Int());
        defaultList.add(new String2Bool());
        defaultList.add(new String2Enum());
        return defaultList;
    }

    public List<DataValueConvertor> getDataTypeConvertorList() {
        return DataTypeConvertorList;
    }

    public void setDataTypeConvertorList(List<DataValueConvertor> DataTypeConvertorList) {
        List<DataValueConvertor> list = new LinkedList<>(DataTypeConvertorList);
        list.addAll(getDefaultDataTypeConvertorList());
        this.DataTypeConvertorList = list;
    }

    public Object transform(Class<?> srcType, Class<?> targetType, Object data) throws UnsupportedValueException {
        List<DataValueConvertor> convertorList = findConvertor(srcType, targetType);
        if (convertorList.isEmpty()) {
            throw new UnsupportedValueException();
        }
        Object ret = null;
        for (DataValueConvertor DataTypeConvertor : convertorList) {
            try {
                ret = DataTypeConvertor.convert(data);
            } catch (UnsupportedValueException e) {
                continue;
            }
            return ret;
        }
        throw new UnsupportedValueException();
    }

    private List<DataValueConvertor> findConvertor(Class<?> srcType, Class<?> targetType) {
        DataTypeConvertorKey cacheKey = new DataTypeConvertorKey(srcType, targetType);

        List<DataValueConvertor> foundConvertorList = null;

        // find in cache
        if (Configuration.getConfiguration().isCacheEnable()) {
            foundConvertorList = convertorCacheMap.get(cacheKey);
        }

        if (foundConvertorList != null) {
            return foundConvertorList;
        } else {
            foundConvertorList = extractConvertors(srcType, targetType);
            convertorCacheMap.put(cacheKey, foundConvertorList);
            return foundConvertorList;
        }
    }

    private List<DataValueConvertor> extractConvertors(final Class<?> srcType, final Class<?> targetType) {

        List<DataValueConvertor> foundConvertorList = new LinkedList<DataValueConvertor>();

        // find in list as element to element
        for (DataValueConvertor convertor : DataTypeConvertorList) {
            Pair<Class, Class> typePair = extractConvertorTypeInfo(convertor);
            if (typePair == null) {
                continue;
            }
            if (typePair.getLeft().isAssignableFrom(srcType)) {
                if (targetType.isAssignableFrom(typePair.getRight())) {// found one
                    foundConvertorList.add(convertor);
                } else if (convertor instanceof DataValueConvertorTargetTypeConvertable && typePair.getRight().isAssignableFrom(targetType)) {
                    foundConvertorList.add(((DataValueConvertorTargetTypeConvertable) convertor).convert(targetType));
                }
            }
            // @formatter:on
        }

        if (!foundConvertorList.isEmpty()) {
            return foundConvertorList;
        }

        // find as array to array
        if (srcType.isArray() && targetType.isArray()) {

            List<DataValueConvertor> componentConvertorList = findConvertor(srcType.getComponentType(), targetType.getComponentType());
            List<DataValueConvertor> toArrayConvertorList = ListConvertUtil.transform(componentConvertorList,
                    new RowConvertor<DataValueConvertor, DataValueConvertor>() {
                        @Override
                        public DataValueConvertor convert(int rowIndex, final DataValueConvertor originalConvertor) {
                            return new DataValueConvertor() {
                                Pair<Class, Class> typePair = extractConvertorTypeInfo(originalConvertor);

                                @Override
                                public Object convert(Object obj) throws UnsupportedValueException {
                                    if (typePair == null) {
                                        return null;
                                    }

                                    int length = Array.getLength(obj);
                                    Object targetArray = Array.newInstance(targetType.getComponentType(), length);

                                    for (int i = 0; i < length; i++) {
                                        Array.set(targetArray, i, originalConvertor.convert(Array.get(obj, i)));
                                    }
                                    return targetArray;
                                }
                            };
                        }
                    });

            foundConvertorList.addAll(toArrayConvertorList);
        }

        if (!foundConvertorList.isEmpty()) {
            return foundConvertorList;
        }

        // find as element to array
        if (targetType.isArray()) {

            List<DataValueConvertor> componentConvertorList = findConvertor(srcType, targetType.getComponentType());
            List<DataValueConvertor> toArrayConvertorList = ListConvertUtil.transform(componentConvertorList,
                    new RowConvertor<DataValueConvertor, DataValueConvertor>() {
                        @Override
                        public DataValueConvertor convert(int rowIndex, final DataValueConvertor originalConvertor) {
                            return new DataValueConvertor() {
                                private Pair<Class, Class> typePair = extractConvertorTypeInfo(originalConvertor);

                                @Override
                                public Object convert(Object obj) throws UnsupportedValueException {
                                    if (typePair == null) {
                                        return null;
                                    }
                                    Object array = Array.newInstance(targetType.getComponentType(), 1);
                                    Array.set(array, 0, originalConvertor.convert(obj));
                                    return array;
                                }
                            };
                        }
                    });

            foundConvertorList.addAll(toArrayConvertorList);
        }

        if (!foundConvertorList.isEmpty()) {
            return foundConvertorList;
        }

        // find as array to element
        if (srcType.isArray()) {
            List<DataValueConvertor> componentConvertorList = findConvertor(srcType.getComponentType(), targetType);
            List<DataValueConvertor> toArrayConvertorList = ListConvertUtil.transform(componentConvertorList,
                    new RowConvertor<DataValueConvertor, DataValueConvertor>() {
                        @Override
                        public DataValueConvertor convert(int rowIndex, final DataValueConvertor originalConvertor) {
                            return new DataValueConvertor() {
                                @Override
                                public Object convert(Object obj) throws UnsupportedValueException {
                                    int length = Array.getLength(obj);
                                    if (length == 0) {
                                        return null;
                                    } else {
                                        return originalConvertor.convert(Array.get(obj, 0));
                                    }
                                }
                            };
                        }
                    });

            foundConvertorList.addAll(toArrayConvertorList);
        }

        if (!foundConvertorList.isEmpty()) {
            return foundConvertorList;
        }

        return foundConvertorList;
    }

    private Pair<Class, Class> extractConvertorTypeInfo(DataValueConvertor convertor) {
        Type[] intfs = convertor.getClass().getGenericInterfaces();
        Class rawCls;
        for (Type intf : intfs) {
            if (intf instanceof ParameterizedType) {
                ParameterizedType pt = (ParameterizedType) intf;
                rawCls = (Class) pt.getRawType();
                if (rawCls.getName().equals(DataValueConvertor.class.getName()) ||
                        rawCls.getName().equals(DataValueConvertorTargetTypeConvertable.class.getName())) {
                    Type[] typeArgs = pt.getActualTypeArguments();
                    return Pair.of((Class) typeArgs[0], (Class) typeArgs[1]);
                }
            }
        }
        logger.warn("Could not extract type information from DataTypeConvertor:" + convertor.getClass().getName());
        return null;
    }
}
