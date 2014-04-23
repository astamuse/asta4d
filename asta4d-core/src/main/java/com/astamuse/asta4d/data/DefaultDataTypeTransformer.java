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
import com.astamuse.asta4d.data.convertor.DataConvertor;
import com.astamuse.asta4d.data.convertor.String2Bool;
import com.astamuse.asta4d.data.convertor.String2Int;
import com.astamuse.asta4d.data.convertor.String2Long;
import com.astamuse.asta4d.util.collection.ListConvertUtil;
import com.astamuse.asta4d.util.collection.RowConvertor;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class DefaultDataTypeTransformer implements DataTypeTransformer {

    private static final Logger logger = LoggerFactory.getLogger(DefaultDataTypeTransformer.class);

    private static final class DataConvertorKey {
        private String srcTypeName;
        private String targetTypeName;
        private int hashCode;

        DataConvertorKey(Class srcType, Class targetType) {
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
            DataConvertorKey other = (DataConvertorKey) obj;
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

    private Map<DataConvertorKey, List<DataConvertor>> convertorCacheMap = new ConcurrentHashMap<>();

    private List<DataConvertor> dataConvertorList = getDefaultDataConvertorList();

    private final static List<DataConvertor> getDefaultDataConvertorList() {
        List<DataConvertor> defaultList = new ArrayList<>();
        defaultList.add(new String2Long());
        defaultList.add(new String2Int());
        defaultList.add(new String2Bool());
        return defaultList;
    }

    public List<DataConvertor> getDataConvertorList() {
        return dataConvertorList;
    }

    public void setDataConvertorList(List<DataConvertor> dataConvertorList) {
        List<DataConvertor> list = new LinkedList<>(dataConvertorList);
        list.addAll(getDefaultDataConvertorList());
        this.dataConvertorList = list;
    }

    public Object transform(Class<?> srcType, Class<?> targetType, Object data) throws DataOperationException {
        List<DataConvertor> convertorList = findConvertor(srcType, targetType);
        if (convertorList.isEmpty()) {
            return null;
        }
        Object ret;
        for (DataConvertor dataConvertor : convertorList) {
            ret = dataConvertor.convert(data);
            if (ret != null) {
                return ret;
            }
        }
        return null;
    }

    private List<DataConvertor> findConvertor(Class<?> srcType, Class<?> targetType) {
        DataConvertorKey cacheKey = new DataConvertorKey(srcType, targetType);

        List<DataConvertor> foundConvertorList = null;

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

    private List<DataConvertor> extractConvertors(Class<?> srcType, Class<?> targetType) {

        List<DataConvertor> foundConvertorList = new LinkedList<DataConvertor>();

        // find in list as element to element
        for (DataConvertor convertor : dataConvertorList) {
            Pair<Class, Class> typePair = extractConvertorTypeInfo(convertor);
            if (typePair == null) {
                continue;
            }
            if (typePair.getLeft().isAssignableFrom(srcType) && targetType.isAssignableFrom(typePair.getRight())) {// found one
                foundConvertorList.add(convertor);
            }
        }

        if (!foundConvertorList.isEmpty()) {
            return foundConvertorList;
        }

        // find as array to array
        if (srcType.isArray() && targetType.isArray()) {

            List<DataConvertor> componentConvertorList = findConvertor(srcType.getComponentType(), targetType.getComponentType());
            List<DataConvertor> toArrayConvertorList = ListConvertUtil.transform(componentConvertorList,
                    new RowConvertor<DataConvertor, DataConvertor>() {
                        @Override
                        public DataConvertor convert(int rowIndex, final DataConvertor originalConvertor) {
                            return new DataConvertor() {
                                Pair<Class, Class> typePair = extractConvertorTypeInfo(originalConvertor);

                                @Override
                                public Object convert(Object obj) {
                                    if (typePair == null) {
                                        return null;
                                    }

                                    int length = Array.getLength(obj);
                                    Object targetArray = Array.newInstance(typePair.getRight(), length);

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

            List<DataConvertor> componentConvertorList = findConvertor(srcType, targetType.getComponentType());
            List<DataConvertor> toArrayConvertorList = ListConvertUtil.transform(componentConvertorList,
                    new RowConvertor<DataConvertor, DataConvertor>() {
                        @Override
                        public DataConvertor convert(int rowIndex, final DataConvertor originalConvertor) {
                            return new DataConvertor() {
                                private Pair<Class, Class> typePair = extractConvertorTypeInfo(originalConvertor);

                                @Override
                                public Object convert(Object obj) {
                                    if (typePair == null) {
                                        return null;
                                    }
                                    Object array = Array.newInstance(typePair.getRight(), 1);
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
            List<DataConvertor> componentConvertorList = findConvertor(srcType.getComponentType(), targetType);
            List<DataConvertor> toArrayConvertorList = ListConvertUtil.transform(componentConvertorList,
                    new RowConvertor<DataConvertor, DataConvertor>() {
                        @Override
                        public DataConvertor convert(int rowIndex, final DataConvertor originalConvertor) {
                            return new DataConvertor() {
                                @Override
                                public Object convert(Object obj) {
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

    private Pair<Class, Class> extractConvertorTypeInfo(DataConvertor convertor) {
        Type[] intfs = convertor.getClass().getGenericInterfaces();
        Class rawCls;
        for (Type intf : intfs) {
            if (intf instanceof ParameterizedType) {
                ParameterizedType pt = (ParameterizedType) intf;
                rawCls = (Class) pt.getRawType();
                if (rawCls.getName().equals(DataConvertor.class.getName())) {
                    Type[] typeArgs = pt.getActualTypeArguments();
                    return Pair.of((Class) typeArgs[0], (Class) typeArgs[1]);
                }
            }
        }
        logger.warn("Could not extract type information from DataConvertor:" + convertor.getClass().getName());
        return null;
    }
}
