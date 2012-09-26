package com.astamuse.asta4d.data;

/**
 * A DataOperationException is thrown when error occurs in injection process.
 * 
 * @author e-ryu
 * 
 */
public class DataOperationException extends Exception {

    private static final long serialVersionUID = 7731788993198703931L;

    public DataOperationException(String message, Throwable cause) {
        super(message, cause);
    }

    public DataOperationException(String message) {
        super(message);
    }

}
