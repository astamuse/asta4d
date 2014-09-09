package com.astamuse.asta4d.data.convertor;

import org.apache.commons.lang3.StringUtils;

/**
 * convert string to enum
 * 
 * @author e-ryu
 * 
 */
@SuppressWarnings("rawtypes")
public class String2Enum implements DataValueConvertorTargetTypeConvertable<String, Enum> {
    @Override
    public DataValueConvertor<String, Enum> convert(final Class<Enum> targetType) {
        return new DataValueConvertor<String, Enum>() {
            @SuppressWarnings("unchecked")
            @Override
            public Enum convert(String s) throws UnsupportedValueException {
                if (StringUtils.isEmpty(s)) {
                    return null;
                }
                return Enum.valueOf(targetType, s);
            }
        };
    }

}
