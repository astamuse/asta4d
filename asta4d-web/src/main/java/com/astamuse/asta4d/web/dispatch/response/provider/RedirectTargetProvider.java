package com.astamuse.asta4d.web.dispatch.response.provider;

import java.util.Map;

import com.astamuse.asta4d.web.dispatch.response.writer.ContentWriter;
import com.astamuse.asta4d.web.dispatch.response.writer.RedirectActionWriter;

public class RedirectTargetProvider implements ContentProvider<RedirectDescriptor> {

    public final static String FlashScopeDataContextKey = RedirectTargetProvider.class + "##FlashScopeDataContextKey";

    private String targetPath;

    private Map<String, Object> flashScopeData;

    public RedirectTargetProvider() {
        this(null, null);
    }

    public RedirectTargetProvider(String targetPath) {
        this(targetPath, null);
    }

    public RedirectTargetProvider(String targetPath, Map<String, Object> flashScopeData) {
        this.targetPath = targetPath;
        this.flashScopeData = flashScopeData;
    }

    public String getTargetPath() {
        return targetPath;
    }

    public void setTargetPath(String targetPath) {
        this.targetPath = targetPath;
    }

    public Map<String, Object> getFlashScopeData() {
        return flashScopeData;
    }

    public void setFlashScopeData(Map<String, Object> flashScopeData) {
        this.flashScopeData = flashScopeData;
    }

    @Override
    public boolean isContinuable() {
        return false;
    }

    @Override
    public RedirectDescriptor produce() {
        return new RedirectDescriptor(targetPath, flashScopeData);
    }

    @Override
    public Class<? extends ContentWriter<RedirectDescriptor>> getContentWriter() {
        return RedirectActionWriter.class;
    }
}
