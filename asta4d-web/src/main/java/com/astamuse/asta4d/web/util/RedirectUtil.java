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

package com.astamuse.asta4d.web.util;

import static com.astamuse.asta4d.web.WebApplicationContext.SCOPE_FLASH;

import java.util.Map;
import java.util.Map.Entry;

import com.astamuse.asta4d.Context;
import com.astamuse.asta4d.util.IdGenerator;

public class RedirectUtil {
    public static final String KEY_FLASH_SCOPE_ID = "flash_scope_id";

    public static String setFlashScopeData(String url, Map<String, Object> flashScopeData) {
        if (flashScopeData == null || flashScopeData.isEmpty()) {
            return url;
        }
        String flashScopeId = IdGenerator.createId();
        FlashScopeDataManager.getInstance().put(flashScopeId, flashScopeData);
        if (url.contains("?")) {
            return url + '&' + KEY_FLASH_SCOPE_ID + '=' + flashScopeId;
        }
        return url + '?' + KEY_FLASH_SCOPE_ID + '=' + flashScopeId;
    }

    public static void getFlashScopeData(String flashScopeId) {
        Context context = Context.getCurrentThreadContext();
        Map<String, Object> flashScopeData = FlashScopeDataManager.getInstance().get(flashScopeId);
        for (Entry<String, Object> entry : flashScopeData.entrySet()) {
            context.setData(SCOPE_FLASH, entry.getKey(), entry.getValue());
        }
    }

}
