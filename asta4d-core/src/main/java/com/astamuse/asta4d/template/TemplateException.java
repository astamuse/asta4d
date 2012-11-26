package com.astamuse.asta4d.template;

public class TemplateException extends Exception {

    /**
     * 
     */
    private static final long serialVersionUID = 365824897144710952L;

    public TemplateException() {
    }

    public TemplateException(String message) {
        super(message);
    }

    public TemplateException(Throwable cause) {
        super(cause);
    }

    public TemplateException(String message, Throwable cause) {
        super(message, cause);
    }

    public TemplateException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
