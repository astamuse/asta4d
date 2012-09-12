package com.astamuse.asta4d.data;

public class DataOperationException extends Exception {

    /**
     * 
     */
    private static final long serialVersionUID = 7731788993198703931L;

    public DataOperationException() {
        super();
    }

    public DataOperationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public DataOperationException(String message, Throwable cause) {
        super(message, cause);
    }

    public DataOperationException(String message) {
        super(message);
    }

    public DataOperationException(Throwable cause) {
        super(cause);
    }
}
