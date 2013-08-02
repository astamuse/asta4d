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

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import com.astamuse.asta4d.Context;
import com.astamuse.asta4d.web.WebApplicationContext;
import com.astamuse.asta4d.web.dispatch.mapping.UrlMappingRule;
import com.astamuse.asta4d.web.util.redirect.RedirectUtil;

public class RedirectTargetProvider implements ContentProvider<RedirectDescriptor> {

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

    private RedirectDescriptor descriptor;

    public RedirectTargetProvider() {
        //
    }

    public RedirectTargetProvider(RedirectDescriptor descriptor) {
        this.descriptor = descriptor;
    }

    public void setDescriptor(RedirectDescriptor descriptor) {
        this.descriptor = descriptor;
    }

    @Override
    public boolean isContinuable() {
        return descriptor.getTargetPath() == null;
    }

    @Override
    public void produce(UrlMappingRule currentRule, HttpServletResponse response) throws Exception {
        RedirectDescriptor rd = (RedirectDescriptor) descriptor;
        String url = rd.getTargetPath();
        Map<String, Object> flashScopeData = rd.getFlashScopeData();
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

            response.setStatus(descriptor.getStatus());
            response.addHeader("Location", url);
        }
    }
}
