package com.astamuse.asta4d.web.dispatch.response.provider;

import java.util.Map;

public class RedirectDescriptor {

    private String targetPath;
    private Map<String, Object> flashScopeData;

    public RedirectDescriptor(String targetPath, Map<String, Object> flashScopeData) {
        this.targetPath = targetPath;
        this.flashScopeData = flashScopeData;
    }

    public String getTargetPath() {
        return targetPath;
    }

    public Map<String, Object> getFlashScopeData() {
        return flashScopeData;
    }

}
