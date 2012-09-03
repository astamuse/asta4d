package org.jsoupit.template;

public class JsoupitException extends RuntimeException {

    /**
     * 
     */
    private static final long serialVersionUID = 8520963634308612549L;

    public JsoupitException() {
        super();
    }

    public JsoupitException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public JsoupitException(String message, Throwable cause) {
        super(message, cause);
    }

    public JsoupitException(String message) {
        super(message);
    }

    public JsoupitException(Throwable cause) {
        super(cause);
    }

}
