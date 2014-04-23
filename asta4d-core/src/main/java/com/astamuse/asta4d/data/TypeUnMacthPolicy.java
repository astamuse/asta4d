package com.astamuse.asta4d.data;

/**
 * The policy of how to handle the unmatched type on data conversion.
 * 
 * @author e-ryu
 * 
 */
public enum TypeUnMacthPolicy {

    /**
     * throw exception
     */
    EXCEPTION,

    /**
     * assign default value to target
     */
    DEFAULT_VALUE,

    /**
     * assign default value to target, also save the trace information in context
     */
    DEFAULT_VALUE_AND_TRACE
}
