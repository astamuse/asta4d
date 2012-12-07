/*
 * Copyright 2012 astamuse company,Ltd.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */

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
