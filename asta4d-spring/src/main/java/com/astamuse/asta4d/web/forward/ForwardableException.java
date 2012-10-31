package com.astamuse.asta4d.web.forward;

@SuppressWarnings("serial")
public class ForwardableException extends RuntimeException {

    private final ForwardDescriptor forwardDescriptor;

    public ForwardableException(ForwardDescriptor forwardDescriptor, Exception cause) {
        super(cause);
        this.forwardDescriptor = forwardDescriptor;
    }

    public ForwardDescriptor getForwardDescriptor() {
        return forwardDescriptor;
    }

    @Override
    public synchronized Exception getCause() {
        return (Exception) super.getCause();
    }
}
