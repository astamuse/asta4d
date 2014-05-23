package com.astamuse.asta4d.data.convertor;

/**
 * convert string to enum
 * 
 * @author e-ryu
 * 
 */
@SuppressWarnings("rawtypes")
public class String2Enum implements DataTypeConvertorTargetTypeConvertable<String, Enum> {
    @Override
    public DataTypeConvertor<String, Enum> convert(final Class<Enum> targetType) {
        return new DataTypeConvertor<String, Enum>() {
            @SuppressWarnings("unchecked")
            @Override
            public Enum convert(String obj) {
                return Enum.valueOf(targetType, obj);
            }
        };
    }

}
