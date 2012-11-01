package com.astamuse.asta4d.web.dispatch.response.forward;

public class ForwardableException extends RuntimeException {

    /**
     * 
     */
    private static final long serialVersionUID = -4366099909329048101L;

    private final ForwardDescriptor forwardDescriptor;

    public ForwardableException(ForwardDescriptor forwardDescriptor, Exception cause) {
        super(cause);
        this.forwardDescriptor = forwardDescriptor;
    }

    public ForwardDescriptor getForwardDescriptor() {
        return forwardDescriptor;
    }

    public Exception getCauseException() {
        return (Exception) super.getCause();
    }
}
