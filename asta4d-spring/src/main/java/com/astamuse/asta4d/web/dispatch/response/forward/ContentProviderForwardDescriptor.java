package com.astamuse.asta4d.web.dispatch.response.forward;

import com.astamuse.asta4d.web.dispatch.response.ContentProvider;

public class ContentProviderForwardDescriptor implements ForwardDescriptor {

    private ContentProvider contentProvider;

    public ContentProviderForwardDescriptor() {
    }

    public ContentProviderForwardDescriptor(ContentProvider contentProvider) {
        super();
        this.contentProvider = contentProvider;
    }

    public ContentProvider getContentProvider() {
        return contentProvider;
    }

    public void setContentProvider(ContentProvider contentProvider) {
        this.contentProvider = contentProvider;
    }

}
