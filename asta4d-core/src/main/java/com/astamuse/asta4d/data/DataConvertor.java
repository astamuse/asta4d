package com.astamuse.asta4d.data;

/**
 * 
 * This interface is used by {@link InjectUtil} to convert context data to the
 * appropriate type automatically
 * 
 * @author e-ryu
 * 
 * @param <S>
 *            source type
 * @param <T>
 *            target type
 */
public interface DataConvertor<S, T> {

    /**
     * convert a data from the original type to a certain type
     * 
     * @param obj
     *            the data wanted to be converted
     * @return converted result
     */
    public T convert(S obj);
}
