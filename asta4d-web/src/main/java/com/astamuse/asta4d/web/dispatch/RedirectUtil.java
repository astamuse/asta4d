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

package com.astamuse.asta4d.web.dispatch;

import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

import com.astamuse.asta4d.Context;
import com.astamuse.asta4d.web.WebApplicationConfiguration;
import com.astamuse.asta4d.web.WebApplicationContext;
import com.astamuse.asta4d.web.util.SecureIdGenerator;

public class RedirectUtil {

    // for performance reason
    private static final String KEY_FLASH_SCOPE_ID = WebApplicationConfiguration.getWebApplicationConfiguration()
            .getFlashScopeForwardParameterName();

    private static final long DATA_EXPIRE_TIME_MILLI_SECONDS = 30_000;

    private static final String FlashScopeDataListKey = RedirectUtil.class.getName() + "##FlashScopeDataListKey";

    private static final String RedirectInterceptorMapKey = RedirectUtil.class.getName() + "##RedirectInterceptorMapKey";

    /**
     * <p>
     * register a redirect interceptor to current context with duplicated id check, if there has been an interceptor registered by same id,
     * the later interceptor will be rejected.
     * 
     * <p>
     * <b>NOTE:</b>There is no guaranty about the execution order of registered interceptors.
     * 
     * @param id
     *            duplication check will be skipped when null specified
     * @param interceptor
     * @return true: the given interceptor is registered<br>
     *         false: the given interceptor is no regiestered because the id has been registered
     */
    public static final boolean registerRedirectInterceptor(String id, RedirectInterceptor interceptor) {
        Context context = Context.getCurrentThreadContext();
        Map<String, RedirectInterceptor> map = context.getData(RedirectInterceptorMapKey);
        if (map == null) {
            map = new HashMap<>();
            context.setData(RedirectInterceptorMapKey, map);
        }

        // we don't do duplicated check for null id
        if (id == null) {
            map.put(id, interceptor);
            return true;
        } else {
            // we don't do thread lock because we believe all the operations about redirection should be done at the handler side, which
            // means there is no concurrent issue.
            if (map.get(id) == null) {
                map.put(id, interceptor);
                return true;
            } else {
                return false;
            }
        }

    }

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

    public static void addFlashScopeData(String name, Object data) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put(name, data);
        addFlashScopeData(map);
    }

    public static void redirectToUrlWithSavedFlashScopeData(HttpServletResponse response, int status, String url) {

        // regulate status
        if (status == HttpURLConnection.HTTP_MOVED_PERM || status == HttpURLConnection.HTTP_MOVED_TEMP) {
            //
        } else {
            status = HttpURLConnection.HTTP_MOVED_TEMP;
        }

        // check illegal url
        if (url.indexOf('\n') >= 0 || url.indexOf('\r') >= 0) {
            throw new RuntimeException("illegal redirct url:" + url);
        }

        // before redirect task
        Map<String, RedirectInterceptor> interceptorMap = Context.getCurrentThreadContext().getData(RedirectInterceptorMapKey);
        if (interceptorMap != null) {
            for (RedirectInterceptor interceptor : interceptorMap.values()) {
                interceptor.beforeRedirect();
            }
            addFlashScopeData(RedirectInterceptorMapKey, interceptorMap);
        }

        // create flash data map
        Map<String, Object> dataMap = new HashMap<String, Object>();
        WebApplicationContext context = Context.getCurrentThreadContext();
        List<Map<String, Object>> dataList = context.getData(FlashScopeDataListKey);
        if (dataList != null) {
            for (Map<String, Object> map : dataList) {
                dataMap.putAll(map);
            }
        }

        // save flash data map
        if (!dataMap.isEmpty()) {
            String flashScopeId = SecureIdGenerator.createEncryptedURLSafeId();
            WebApplicationConfiguration.getWebApplicationConfiguration().getTimeoutDataManager()
                    .put(flashScopeId, dataMap, DATA_EXPIRE_TIME_MILLI_SECONDS);

            // create target url
            if (url.contains("?")) {
                url = url + '&' + KEY_FLASH_SCOPE_ID + '=' + flashScopeId;
            } else {
                url = url + '?' + KEY_FLASH_SCOPE_ID + '=' + flashScopeId;
            }
        }

        // do redirection
        response.setStatus(status);
        response.addHeader("Location", url);
    }

    public static void restoreFlashScopeData(HttpServletRequest request) {
        String flashScopeId = request.getParameter(RedirectUtil.KEY_FLASH_SCOPE_ID);
        if (StringUtils.isEmpty(flashScopeId)) {
            return;
        } else {
            Map<String, Object> savedMap = WebApplicationConfiguration.getWebApplicationConfiguration().getTimeoutDataManager()
                    .get(flashScopeId);
            if (savedMap == null) {
                return;
            } else {
                WebApplicationContext context = WebApplicationContext.getCurrentThreadWebApplicationContext();
                for (Entry<String, Object> entry : savedMap.entrySet()) {
                    context.setData(WebApplicationContext.SCOPE_FLASH, entry.getKey(), entry.getValue());
                }
                Map<String, RedirectInterceptor> interceptorMap = context.getData(WebApplicationContext.SCOPE_FLASH,
                        RedirectInterceptorMapKey);
                if (interceptorMap != null) {
                    for (RedirectInterceptor interceptor : interceptorMap.values()) {
                        interceptor.afterRedirectDataRestore();
                    }
                }
            }
        }

    }

}
