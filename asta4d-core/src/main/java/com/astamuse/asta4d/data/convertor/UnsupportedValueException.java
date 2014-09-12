package com.astamuse.asta4d.data.convertor;

import com.astamuse.asta4d.data.DataOperationException;
import com.astamuse.asta4d.data.DefaultDataTypeTransformer;

/**
 * 
 * A {@link DataValueConvertor} would throw this exception to show that it cannot convert the given value even the type is matched. The
 * {@link DefaultDataTypeTransformer} would ignore this exception and try the left convertors in the list.
 * 
 * @author e-ryu
 * 
 */
public class UnsupportedValueException extends DataOperationException {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public UnsupportedValueException() {
        super("");
    }

}
