package com.astamuse.asta4d.web.dispatch.interceptor;

import com.astamuse.asta4d.web.dispatch.response.forward.ForwardDescriptor;

public class ForwardDescriptorHolder {

    private ForwardDescriptor forwardDescriptor;

    public ForwardDescriptorHolder() {
    }

    public ForwardDescriptorHolder(ForwardDescriptor forwardDescriptor) {
        super();
        this.forwardDescriptor = forwardDescriptor;
    }

    public ForwardDescriptor getForwardDescriptor() {
        return forwardDescriptor;
    }

    public void setForwardDescriptor(ForwardDescriptor forwardDescriptor) {
        this.forwardDescriptor = forwardDescriptor;
    }

}
