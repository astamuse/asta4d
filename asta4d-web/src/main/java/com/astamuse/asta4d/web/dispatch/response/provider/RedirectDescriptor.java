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

import java.net.HttpURLConnection;
import java.util.Map;

public class RedirectDescriptor {

    private int status;
    private String targetPath;
    private Map<String, Object> flashScopeData;

    public RedirectDescriptor(String targetPath) {
        this(targetPath, null);
    }

    public RedirectDescriptor(String targetPath, Map<String, Object> flashScopeData) {
        this(HttpURLConnection.HTTP_MOVED_TEMP, targetPath, flashScopeData);
    }

    public RedirectDescriptor(int status, String targetPath, Map<String, Object> flashScopeData) {
        if (status == HttpURLConnection.HTTP_MOVED_PERM || status == HttpURLConnection.HTTP_MOVED_TEMP) {
            this.status = status;
        } else {
            this.status = HttpURLConnection.HTTP_MOVED_TEMP;
        }
        this.targetPath = targetPath;
        this.flashScopeData = flashScopeData;
    }

    public int getStatus() {
        return status;
    }

    public String getTargetPath() {
        return targetPath;
    }

    public Map<String, Object> getFlashScopeData() {
        return flashScopeData;
    }

}
