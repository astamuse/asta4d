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

package com.astamuse.asta4d.web.util.redirect;

import java.util.Collections;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import com.astamuse.asta4d.web.WebApplicationConfiguration;
import com.astamuse.asta4d.web.util.SecureIdGenerator;

public class RedirectUtil {

    // for performance reason
    private static final String KEY_FLASH_SCOPE_ID = WebApplicationConfiguration.getWebApplicationConfiguration()
            .getFlashScopeForwardParameterName();

    public static String setFlashScopeData(String url, Map<String, Object> flashScopeData) {
        if (flashScopeData == null || flashScopeData.isEmpty()) {
            return url;
        }
        String flashScopeId = SecureIdGenerator.createEncryptedURLSafeId();
        FlashScopeDataManager.getInstance().put(flashScopeId, flashScopeData);
        if (url.contains("?")) {
            return url + '&' + KEY_FLASH_SCOPE_ID + '=' + flashScopeId;
        } else {
            return url + '?' + KEY_FLASH_SCOPE_ID + '=' + flashScopeId;
        }
    }

    public static Map<String, Object> retrieveFlashScopeData(HttpServletRequest request) {

        String flashScopeId = request.getParameter(RedirectUtil.KEY_FLASH_SCOPE_ID);
        if (StringUtils.isEmpty(flashScopeId)) {
            return Collections.emptyMap();
        } else {
            return FlashScopeDataManager.getInstance().get(flashScopeId);
        }

    }

}
