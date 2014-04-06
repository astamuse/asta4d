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
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import com.astamuse.asta4d.Context;
import com.astamuse.asta4d.web.WebApplicationContext;
import com.astamuse.asta4d.web.dispatch.mapping.UrlMappingRule;
import com.astamuse.asta4d.web.util.redirect.RedirectUtil;

public class RedirectTargetProvider implements ContentProvider {

    private static final String FlashScopeDataListKey = RedirectTargetProvider.class.getName() + "##FlashScopeDataListKey";

    public static void addFlashScopeData(Map<String, Object> flashScopeData) {
        if (flashScopeData == null || flashScopeData.isEmpty()) {
            return;
        }
        WebApplicationContext context = Context.getCurrentThreadContext();
        List<Map<String, Object>> dataList = context.getData(FlashScopeDataListKey);
        if (dataList == null) {
            dataList = new LinkedList<>();
            context.setData(FlashScopeDataListKey, dataList);
        }
        dataList.add(flashScopeData);
    }

    private int status;
    private String targetPath;
    private Map<String, Object> flashScopeData;

    public RedirectTargetProvider() {
        //
    }

    public RedirectTargetProvider(String targetPath) {
        this(targetPath, null);
    }

    public RedirectTargetProvider(String targetPath, Map<String, Object> flashScopeData) {
        this(HttpURLConnection.HTTP_MOVED_TEMP, targetPath, flashScopeData);
    }

    public RedirectTargetProvider(int status, String targetPath, Map<String, Object> flashScopeData) {
        this.targetPath = targetPath;
        this.flashScopeData = flashScopeData;
        this.setStatus(status);
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        if (status == HttpURLConnection.HTTP_MOVED_PERM || status == HttpURLConnection.HTTP_MOVED_TEMP) {
            this.status = status;
        } else {
            this.status = HttpURLConnection.HTTP_MOVED_TEMP;
        }
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
        return targetPath == null;
    }

    @Override
    public void produce(UrlMappingRule currentRule, HttpServletResponse response) throws Exception {
        String url = targetPath;
        if (url == null) {
            addFlashScopeData(flashScopeData);
        } else {
            Map<String, Object> dataMap = new HashMap<String, Object>();
            WebApplicationContext context = Context.getCurrentThreadContext();
            List<Map<String, Object>> dataList = context.getData(FlashScopeDataListKey);
            if (dataList != null) {
                for (Map<String, Object> map : dataList) {
                    dataMap.putAll(map);
                }
            }
            if (flashScopeData != null) {
                dataMap.putAll(flashScopeData);
            }
            if (url.startsWith("/")) {
                url = context.getRequest().getContextPath() + url;
            }
            url = RedirectUtil.setFlashScopeData(url, dataMap);

            if (url.indexOf('\n') >= 0 || url.indexOf('\r') >= 0) {
                throw new RuntimeException("illegal redirct url:" + url);
            }

            response.setStatus(status);
            response.addHeader("Location", url);
        }
    }
}
