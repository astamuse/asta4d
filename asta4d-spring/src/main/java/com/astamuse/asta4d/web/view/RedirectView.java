package com.astamuse.asta4d.web.view;

public class RedirectView implements Asta4dView {

    private final String url;

    // TODO determine whether the required parameters
    private final boolean contextRelative;

    private final boolean http10Compatible;

    private final boolean exposeModelAttributes;

    public RedirectView(String url) {
        this(url, false);
    }

    public RedirectView(String url, boolean contextRelative) {
        this(url, contextRelative, true);
    }

    public RedirectView(String url, boolean contextRelative, boolean http10Compatible) {
        this(url, contextRelative, http10Compatible, true);
    }

    public RedirectView(String url, boolean contextRelative, boolean http10Compatible, boolean exposeModelAttributes) {
        this.url = url;
        this.contextRelative = contextRelative;
        this.http10Compatible = http10Compatible;
        this.exposeModelAttributes = exposeModelAttributes;
    }

    public String getUrl() {
        return url;
    }

    public boolean isContextRelative() {
        return contextRelative;
    }

    public boolean isHttp10Compatible() {
        return http10Compatible;
    }

    public boolean isExposeModelAttributes() {
        return exposeModelAttributes;
    }
}
